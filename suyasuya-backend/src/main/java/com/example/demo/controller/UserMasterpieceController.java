package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Video;
import com.example.demo.service.UserMasterpieceService;
import com.example.demo.vo.MasterpieceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserMasterpieceController {
    @Autowired
    private UserMasterpieceService userMasterpieceService;

    @PostMapping("/setMasterpiece")
    public ResponseEntity<Result<?>> handleMasterpiece(@RequestAttribute Integer userId, @RequestParam Integer videoId) {
        try {
            userMasterpieceService.saveOrUpdateMasterpiece(
                    userId,
                    videoId
            );
            return ResponseEntity.ok(Result.success("操作成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.failure(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Result.failure("服务器繁忙，请稍后重试"));
        }
    }

    @GetMapping("/getMasterpiece")
    public ResponseEntity<Result<MasterpieceVO>> getMasterpiece(@RequestParam Integer userId) {
        try {
            Video video = userMasterpieceService.getMasterpieceVideo(userId);

            if (video == null) {
                return ResponseEntity.ok(Result.success("用户尚未设置代表作", null));
            }

            // 转换为前端需要的VO对象
            MasterpieceVO vo = convertToVO(video);
            return ResponseEntity.ok(Result.success("查询成功", vo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.failure(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Result.failure(e.getMessage()));
        }
    }

    // 转换方法示例
    private MasterpieceVO convertToVO(Video video) {
        MasterpieceVO vo = new MasterpieceVO();
        vo.setVideoId(video.getVideoId());
        vo.setCover(video.getCover());
        vo.setDuration(video.getDuration());
        vo.setTitle(video.getTitle());
        vo.setViewCount(video.getViewCount());
        vo.setUploadTime(video.getUploadTime());
        vo.setIntroduction(video.getIntroduction());
        return vo;
    }
}
