import request from '@/utils/request'

export const getProductList = (params) => request.get('/product/list', { params })
export const getProductDetail = (id) => request.get(`/product/${id}`)
export const getHotProducts = () => request.get('/product/hot')
export const getRecommendProducts = () => request.get('/product/recommend')
