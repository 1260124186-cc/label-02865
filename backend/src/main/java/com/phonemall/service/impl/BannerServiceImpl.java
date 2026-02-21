package com.phonemall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.phonemall.entity.Banner;
import com.phonemall.mapper.BannerMapper;
import com.phonemall.service.BannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerMapper bannerMapper;

    @Override
    public List<Banner> listBanners() {
        return bannerMapper.selectList(
                new LambdaQueryWrapper<Banner>()
                        .eq(Banner::getStatus, 1)
                        .orderByAsc(Banner::getSortOrder));
    }

    @Override
    public List<Banner> adminListBanners() {
        return bannerMapper.selectList(
                new LambdaQueryWrapper<Banner>().orderByAsc(Banner::getSortOrder));
    }

    @Override
    public void saveBanner(Banner banner) {
        if (banner.getId() != null) {
            bannerMapper.updateById(banner);
            log.info("更新轮播图: id={}", banner.getId());
        } else {
            bannerMapper.insert(banner);
            log.info("新增轮播图");
        }
    }

    @Override
    public void deleteBanner(Long id) {
        bannerMapper.deleteById(id);
        log.info("删除轮播图: id={}", id);
    }
}
