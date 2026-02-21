import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('@/views/layout/MobileLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/home/HomePage.vue') },
      { path: 'category', name: 'Category', component: () => import('@/views/category/CategoryPage.vue') },
      { path: 'cart', name: 'Cart', component: () => import('@/views/cart/CartPage.vue') },
      { path: 'user', name: 'User', component: () => import('@/views/user/UserPage.vue') },
    ]
  },
  { path: '/product/:id', name: 'ProductDetail', component: () => import('@/views/product/ProductDetail.vue') },
  { path: '/search', name: 'Search', component: () => import('@/views/home/SearchPage.vue') },
  { path: '/order/list', name: 'OrderList', component: () => import('@/views/user/OrderList.vue') },
  { path: '/order/checkout', name: 'Checkout', component: () => import('@/views/order/CheckoutPage.vue') },
  { path: '/address', name: 'Address', component: () => import('@/views/order/AddressPage.vue') },
  { path: '/login', name: 'Login', component: () => import('@/views/user/LoginPage.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (['/cart', '/order/list', '/order/checkout'].includes(to.path) && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
