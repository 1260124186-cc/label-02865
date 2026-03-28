import request from '@/utils/request'

export const getProductReviews = (productId, params) => 
  request.get(`/review/product/${productId}`, { params })

export const getAverageRating = (productId) => 
  request.get(`/review/product/${productId}/rating`)

export const createReview = (data) => 
  request.post('/review/create', data)

export const replyToReview = (data) => 
  request.post('/review/reply', data)

export const canReview = (params) => 
  request.get('/review/can-review', { params })
