package com.example.demo.controller;

import com.example.demo.common.PageResult;
import com.example.demo.common.Result;
import com.example.demo.service.WatchHistoryService;
import com.example.demo.vo.WatchHistoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
public class WatchHistoryController {
    @Autowired
    private WatchHistoryService watchHistoryService;

    /**
     * 记录观看历史接口
     *
     * @param userId  从请求头或Token获取
     * @param videoId 路径参数
     */
    @PostMapping("/recordWatchHistory")
    public Result<?> recordWatchHistory(
            @RequestAttribute Integer userId,
            @RequestParam Integer videoId
    ) {
        try {
            // 参数校验
            if (userId == null || userId <= 0) {
                return Result.failure("用户身份验证失败");
            }
            if (videoId == null || videoId <= 0) {
                return Result.failure("视频ID不合法");
            }
            // 业务处理
            watchHistoryService.recordWatchHistory(userId, videoId);
            return Result.success("观看记录更新成功");

        } catch (RuntimeException e) {
            return Result.failure(e.getMessage());
        } catch (Exception e) {
            return Result.failure("系统繁忙，请稍后重试");
        }
    }

    @GetMapping("/getWatchHistory")
    public Result<PageResult<WatchHistoryVO>> getWatchHistory(
            @RequestAttribute Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        PageResult<WatchHistoryVO> result =
                watchHistoryService.getWatchHistory(userId, page, size);
        return Result.success("历史记录获取成功", result);
    }

    @DeleteMapping("/delete")
    public Result<Void> deleteWatchHistory(
            @RequestAttribute Integer userId, // 从拦截器获取的真实用户ID
            @RequestParam Integer videoId
    ) {
        watchHistoryService.deleteHistory(userId, videoId);
        return Result.success("删除成功");
    }

    @DeleteMapping("/clear")
    public Result<Void> clearHistory(@RequestAttribute Integer userId) {
        watchHistoryService.clearHistory(userId);
        return Result.success("历史记录已清空");
    }
}
