package com.sachdeva.Aibot.Repositories;

import com.sachdeva.Aibot.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User,Long> {

}
