import request from '@/utils/request'

export const getCategoryList = () => request.get('/category/list')
