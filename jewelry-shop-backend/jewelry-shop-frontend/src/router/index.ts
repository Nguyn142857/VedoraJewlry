import { createRouter, createWebHistory } from 'vue-router'

import AccountLayout from '@/layouts/account/AccountLayout.vue'
import AdminLayout from '@/layouts/admin/AdminLayout.vue'
import PublicLayout from '@/layouts/public/PublicLayout.vue'
import { requireAdmin, requireAuth } from '@/router/guards/auth'
import AdminCategoryPage from '@/modules/admin/categories/pages/AdminCategoryPage.vue'
import AdminDashboardPage from '@/modules/admin/dashboard/pages/AdminDashboardPage.vue'
import AdminImagePage from '@/modules/admin/images/pages/AdminImagePage.vue'
import AdminOrderPage from '@/modules/admin/orders/pages/AdminOrderPage.vue'
import AdminProductPage from '@/modules/admin/products/pages/AdminProductPage.vue'
import AdminVariantPage from '@/modules/admin/variants/pages/AdminVariantPage.vue'
import ForgotPasswordPage from '@/modules/auth/pages/ForgotPasswordPage.vue'
import LoginPage from '@/modules/auth/pages/LoginPage.vue'
import RegisterPage from '@/modules/auth/pages/RegisterPage.vue'
import CartPage from '@/modules/cart/pages/CartPage.vue'
import HomePage from '@/modules/catalog/pages/HomePage.vue'
import ProductDetailPage from '@/modules/catalog/pages/ProductDetailPage.vue'
import ProductListPage from '@/modules/catalog/pages/ProductListPage.vue'
import CheckoutPage from '@/modules/checkout/pages/CheckoutPage.vue'
import OrderDetailPage from '@/modules/orders/pages/OrderDetailPage.vue'
import OrderListPage from '@/modules/orders/pages/OrderListPage.vue'
import ProfilePage from '@/modules/profile/pages/ProfilePage.vue'
import ForbiddenPage from '@/modules/system/pages/ForbiddenPage.vue'
import NotFoundPage from '@/modules/system/pages/NotFoundPage.vue'
import UnauthorizedPage from '@/modules/system/pages/UnauthorizedPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: PublicLayout,
      children: [
        { path: '', name: 'home', component: HomePage },
        { path: 'products', name: 'products', component: ProductListPage },
        { path: 'products/:slug', name: 'product-detail', component: ProductDetailPage },
        { path: 'login', name: 'login', component: LoginPage },
        { path: 'register', name: 'register', component: RegisterPage },
        { path: 'forgot-password', name: 'forgot-password', component: ForgotPasswordPage },
        { path: 'unauthorized', name: 'unauthorized', component: UnauthorizedPage },
        { path: 'forbidden', name: 'forbidden', component: ForbiddenPage },
        { path: 'cart', name: 'cart', component: CartPage, beforeEnter: requireAuth },
        { path: 'checkout', name: 'checkout', component: CheckoutPage, beforeEnter: requireAuth },
        { path: ':pathMatch(.*)*', name: 'not-found', component: NotFoundPage },
      ],
    },
    {
      path: '/account',
      component: AccountLayout,
      beforeEnter: requireAuth,
      children: [
        { path: 'profile', name: 'profile', component: ProfilePage },
        { path: 'orders', name: 'orders', component: OrderListPage },
        { path: 'orders/:id', name: 'order-detail', component: OrderDetailPage },
      ],
    },
    {
      path: '/admin',
      component: AdminLayout,
      beforeEnter: requireAdmin,
      children: [
        { path: '', name: 'admin-dashboard', component: AdminDashboardPage },
        { path: 'categories', name: 'admin-categories', component: AdminCategoryPage },
        { path: 'products', name: 'admin-products', component: AdminProductPage },
        { path: 'variants', name: 'admin-variants', component: AdminVariantPage },
        { path: 'images', name: 'admin-images', component: AdminImagePage },
        { path: 'orders', name: 'admin-orders', component: AdminOrderPage },
      ],
    },
  ],
  scrollBehavior() {
    return { top: 0 }
  },
})

export default router
