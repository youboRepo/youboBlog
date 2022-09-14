package com.youbo.util.upload.channel;

import com.upyun.RestManager;
import com.youbo.config.properties.UpyunProperties;
import com.youbo.util.upload.UploadUtils;
import okhttp3.Response;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 又拍云存储上传方式
 *
 * @author: youbo
 * @date: 2022-05-26
 */
@Lazy
@Component
public class UpyunChannel implements FileUploadChannel {
	private RestManager manager;
	private UpyunProperties upyunProperties;

	public UpyunChannel(UpyunProperties upyunProperties) {
		this.upyunProperties = upyunProperties;
		this.manager = new RestManager(upyunProperties.getBucketName(), upyunProperties.getUsername(), upyunProperties.getPassword());
	}

	@Override
	public String upload(UploadUtils.ImageResource image) throws Exception {
		String fileAbsolutePath = upyunProperties.getPath() + "/" + UUID.randomUUID() + "." + image.getType();
		Response response = manager.writeFile(fileAbsolutePath, image.getData(), null);
		if (!response.isSuccessful()) {
			throw new RuntimeException("又拍云上传失败");
		}
		return upyunProperties.getDomain() + fileAbsolutePath;
	}
}
