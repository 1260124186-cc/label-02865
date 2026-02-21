import request from '@/utils/request'

export const getCartList = () => request.get('/cart/list')
export const addCart = (data) => request.post('/cart/add', data)
export const updateCart = (id, quantity) => request.put('/cart/update', null, { params: { id, quantity } })
export const deleteCart = (id) => request.delete(`/cart/delete/${id}`)
export const selectAllCart = (selected) => request.put('/cart/select-all', null, { params: { selected } })
