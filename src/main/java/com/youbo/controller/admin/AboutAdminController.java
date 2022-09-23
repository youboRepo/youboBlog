package com.youbo.controller.admin;

import com.youbo.annotation.OperationLogger;
import com.youbo.entity.About;
import com.youbo.query.AboutQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.youbo.model.vo.Result;
import com.youbo.service.AboutService;
import org.thymeleaf.util.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description: 关于我页面后台管理
 * @Author: youbo
 * @Date: 2020-09-01
 */
@RestController
@RequestMapping("/admin")
public class AboutAdminController {
	@Autowired
	private AboutService aboutService;

	/**
	 * 获取关于我页面配置
	 *
	 * @return
	 */
	@GetMapping("/about")
	public Result about() {
		
		// 获取关于我映射
		Map<String, String> aboutSetting = aboutService.getAboutSetting();

		return Result.ok("请求成功", aboutSetting);
	}

	/**
	 * 修改关于我页面
	 *
	 * @param map
	 * @return
	 */
	@OperationLogger("修改关于我页面")
	@PutMapping("/about")
	public Result updateAbout(@RequestBody Map<String, String> map) {
		List<String> nameEns = map.keySet().stream().collect(Collectors.toList());
		AboutQuery query = new AboutQuery();
		query.setNameEns(nameEns);
		
		// 获取关于映射
		Map<String, About> aboutMap = aboutService.getAboutInfos(query).stream()
				.collect(Collectors.toMap(About::getNameEn, Function.identity()));
		
		if (MapUtils.isEmpty(aboutMap)) {
			return Result.error("修改失败,不存在修改对象");
		}
		
		List<About> updateAbouts = new ArrayList<>();
		aboutMap.forEach((nameEn, about) -> {
			About updateAbout = new About();
			updateAbout.setId(about.getId());
			updateAbout.setValue(map.get(nameEn));
			updateAbouts.add(updateAbout);
		});

		aboutService.updateAbout(updateAbouts);
		return Result.ok("修改成功");
	}
}
