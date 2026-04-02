package com.jewelry.jewelryshopbackend.service.user;

import com.jewelry.jewelryshopbackend.entity.User;

public interface CurrentUserService {

    User requireCurrentUser();
}
