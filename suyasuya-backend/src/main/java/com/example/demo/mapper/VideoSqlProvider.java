package com.example.demo.mapper;

import org.apache.ibatis.jdbc.SQL;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// SQL动态构建器 -- 相关视频推荐视频
public class VideoSqlProvider {

    public String buildRecommendSql(Map<String, Object> params) {
        List<String> tags = (List<String>) params.get("tags");
        Double tagWeight = (Double) params.get("tagWeight");
        Double playWeight = (Double) params.get("playWeight");
        Integer limit = (Integer) params.get("limit");

        return new SQL() {{
            SELECT("v.video_id", "v.cover", "v.author_id", "v.author_name", "v.upload_time", "v.title", "v.duration", "v.view_count");
            SELECT("(SUM(t.related_count * #{tagWeight}) + " + "(v.view_count * #{playWeight})) AS score");
            FROM("videos v");
            INNER_JOIN("video_tag_relations r ON v.video_id = r.video_id");
            INNER_JOIN("video_tags t ON r.tag_id = t.tag_id");
            WHERE("v.status = 'published'");
            WHERE("v.video_id != #{videoId}");

            if (tags != null && !tags.isEmpty()) {
                String inClause = "t.tag_name IN (" + tags.stream().map(t -> "'" + t.replace("'", "''") + "'") // 防SQL注入
                        .collect(Collectors.joining(",")) + ")";
                WHERE(inClause);
            }

            GROUP_BY("v.video_id");
            ORDER_BY("score DESC");
            LIMIT(limit);
        }}.toString();
    }
    public String buildSearchSql(Map<String, Object> params) {
        // 从参数中提取排序字段和方向，设置默认值
        String sortField = validateSortField((String) params.get("sortField"));
        String sortOrder = validateSortOrder((String) params.get("sortOrder"));

        return new SQL() {{
            SELECT(" v.video_id", "v.cover", "v.author_id", "v.title",
                    "v.author_name", "v.upload_time", "v.duration", "v.view_count","v.collection_count");
            FROM("videos v");
            LEFT_OUTER_JOIN("video_tag_relations vr ON v.video_id = vr.video_id");
            LEFT_OUTER_JOIN("video_tags t ON vr.tag_id = t.tag_id");
            WHERE("v.status = 'published'");
            WHERE("(v.title LIKE #{keyword} " +
                    "OR v.author_name LIKE #{keyword} " +
                    "OR t.tag_name LIKE #{keyword})");
            GROUP_BY("v.video_id");    // 先GROUP BY
            ORDER_BY("v."+ sortField + " " + sortOrder); // 后ORDER BY
        }}.toString();
    }
    // 校验排序字段并返回合法值（无二次赋值）
    private String validateSortField(String inputSortField) {
        List<String> allowedFields = Arrays.asList("view_count", "upload_time", "favorite_count");
        return allowedFields.contains(inputSortField) ? inputSortField : "view_count";
    }

    // 校验排序方向并返回合法值（无二次赋值）
    private String validateSortOrder(String inputSortOrder) {
        return "ASC".equalsIgnoreCase(inputSortOrder) ? "ASC" : "DESC";
    }
}
