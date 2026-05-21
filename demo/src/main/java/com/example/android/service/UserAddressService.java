package com.example.android.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.android.entity.UserAddress;
import com.example.android.mapper.UserAddressMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAddressService extends ServiceImpl<UserAddressMapper, UserAddress> {
    public List<UserAddress> getAddressesByUserId(Long userId) {
        QueryWrapper<UserAddress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("is_default").orderByDesc("created_at");
        return list(wrapper);
    }

    public boolean addAddress(UserAddress address) {
        if (address.getIsDefault() == 1) {
            QueryWrapper<UserAddress> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", address.getUserId());
            baseMapper.delete(wrapper);
        }
        return save(address);
    }

    public boolean updateAddress(UserAddress address) {
        // 先获取原始地址，确保 userId 正确
        UserAddress existingAddress = getById(address.getId());
        if (existingAddress != null && existingAddress.getUserId() != null) {
            address.setUserId(existingAddress.getUserId());
            
            if (address.getIsDefault() == 1) {
                QueryWrapper<UserAddress> wrapper = new QueryWrapper<>();
                wrapper.eq("user_id", address.getUserId()).ne("id", address.getId());
                UserAddress temp = new UserAddress();
                temp.setIsDefault(0);
                baseMapper.update(temp, wrapper);
            }
        }
        return updateById(address);
    }
}
