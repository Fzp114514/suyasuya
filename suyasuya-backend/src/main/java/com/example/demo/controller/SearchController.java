package com.example.demo.controller;

import com.example.demo.common.PageResult;
import com.example.demo.common.Result;
import com.example.demo.dto.VideoDTO;
import com.example.demo.service.SearchService;
import com.example.demo.vo.VideoSearchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    /**
     * 根据关键词查询视频，支持分页并按播放量排序
     * @param keyword 搜索关键词
     * @param pageNum 搜索页数
     * @param pageSize 每页个数
     * @param sortType 排序方式
     * @return 返回视频列表
     */
    @GetMapping("/videos")
    public Result<PageResult<VideoSearchVO>> searchVideos(
            @RequestAttribute Integer userId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "view") String sortType){

        try {
            PageResult<VideoSearchVO> result = searchService.searchVideos(userId,keyword, pageNum, pageSize, sortType);
            return Result.success("搜索成功", result);
        } catch (IllegalArgumentException e) {
            return Result.failure(e.getMessage());
        } catch (Exception e) {
            System.out.println(e);
            return Result.failure("搜索服务暂不可用");
        }
    }
}
