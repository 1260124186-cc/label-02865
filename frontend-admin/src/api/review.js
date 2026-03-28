import request from '@/utils/request'

/**
 * 评价相关API
 */

// 获取商品评价列表
export function getProductReviews(productId, page = 1, pageSize = 10) {
  return request({
    url: `/api/reviews/product/${productId}`,
    method: 'get',
    params: { page, pageSize }
  })
}

// 创建评价
export function createReview(data) {
  return request({
    url: '/api/reviews',
    method: 'post',
    data
  })
}

// 商家回复评价
export function replyReview(data) {
  return request({
    url: '/api/reviews/reply',
    method: 'post',
    data
  })
}
