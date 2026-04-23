package com.sachdeva.Aibot.Repositories;

import com.sachdeva.Aibot.Model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Commentrepo extends JpaRepository<Comment,Long> {

}
