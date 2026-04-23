package com.sachdeva.Aibot.Repositories;

import com.sachdeva.Aibot.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Postrepo extends JpaRepository<Post,Long> {

}
