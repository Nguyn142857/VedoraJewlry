import { http } from '@/shared/api/http'
import type { UserProfile, UserProfilePayload } from '@/modules/profile/types/profile'

export const profileApi = {
  getMyProfile() {
    return http.get<UserProfile>('/api/user/profile')
  },
  updateMyProfile(payload: UserProfilePayload) {
    return http.put<UserProfile>('/api/user/profile', payload)
  },
}
