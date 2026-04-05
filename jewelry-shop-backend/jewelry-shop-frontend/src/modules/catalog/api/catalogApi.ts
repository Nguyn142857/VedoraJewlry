import { http } from '@/shared/api/http'
import type {
  CategoryPage,
  ProductDetail,
  ProductImagePage,
  ProductListFilters,
  ProductPage,
  VariantPage,
} from '@/modules/catalog/types/catalog'

function buildQuery(params: Record<string, string | number | undefined>) {
  const search = new URLSearchParams()

  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === '') {
      return
    }

    search.set(key, String(value))
  })

  const query = search.toString()
  return query ? `?${query}` : ''
}

export const catalogApi = {
  getCategories() {
    return http.get<CategoryPage>(`/api/categories${buildQuery({ page: 0, size: 100, sortBy: 'name', sortDir: 'asc' })}`)
  },
  getProducts(filters: ProductListFilters) {
    return http.get<ProductPage>(
      `/api/products${buildQuery({
        page: filters.page ?? 0,
        size: filters.size ?? 12,
        q: filters.q,
        categoryId: filters.categoryId,
        minPrice: filters.minPrice,
        maxPrice: filters.maxPrice,
        sortBy: filters.sortBy ?? 'createdAt',
        sortDir: filters.sortDir ?? 'desc',
      })}`,
    )
  },
  getProductDetail(slug: string) {
    return http.get<ProductDetail>(`/api/products/${slug}`)
  },
  getProductVariants(slug: string) {
    return http.get<VariantPage>(`/api/products/${slug}/variants${buildQuery({ page: 0, size: 100, sortBy: 'price', sortDir: 'asc' })}`)
  },
  getProductImages(slug: string) {
    return http.get<ProductImagePage>(`/api/products/${slug}/images${buildQuery({ page: 0, size: 20, sortBy: 'sortOrder', sortDir: 'asc' })}`)
  },
}
