package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.UserCollection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserCollectionMapper extends BaseMapper<UserCollection> {

    int updateVideoCount(@Param("collectionId") Integer collectionId,
                         @Param("increment") int increment);

    // 通过用户ID和收藏夹名称查找收藏夹
    UserCollection selectByUserIdAndName(@Param("userId") Integer userId, @Param("collectionName") String collectionName);
}
