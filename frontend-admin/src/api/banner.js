import request from '@/utils/request'

export const getBannerList = () => request.get('/banner/list')
