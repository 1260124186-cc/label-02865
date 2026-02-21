package com.phonemall.service;

import com.phonemall.entity.Banner;
import java.util.List;

public interface BannerService {
    List<Banner> listBanners();
    List<Banner> adminListBanners();
    void saveBanner(Banner banner);
    void deleteBanner(Long id);
}
