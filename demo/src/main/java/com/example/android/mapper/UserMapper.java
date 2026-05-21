package com.example.android.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.android.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM users WHERE username = #{username}")
    User selectByUsername(String username);

    @Select("SELECT * FROM users WHERE student_id = #{studentId}")
    User selectByStudentId(String studentId);

    @Select("SELECT * FROM users")
    List<User> selectAllUsers();

    @Update("UPDATE users SET password_hash = #{passwordHash} WHERE id = #{userId}")
    int updatePassword(Long userId, String passwordHash);

    @Delete("DELETE FROM users WHERE id = #{userId}")
    int deleteUser(Long userId);

    @Update("UPDATE users SET credit_score = #{creditScore} WHERE id = #{userId}")
    int updateCreditScore(Long userId, Integer creditScore);

    @Update("UPDATE users SET username = #{username}, student_id = #{studentId}, phone = #{phone}, school = #{school}, avatar_url = #{avatarUrl} WHERE id = #{id}")
    int updateUser(User user);
}
