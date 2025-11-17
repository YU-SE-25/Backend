package com.unide.backend.domain.discuss.repository;

import com.unide.backend.domain.discuss.entity.DisPostLike;
import com.unide.backend.domain.discuss.entity.DisPostLikeId;
import com.unide.backend.domain.discuss.entity.Discuss;
import com.unide.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisPostLikeRepository extends JpaRepository<DisPostLike, DisPostLikeId> {

    boolean existsByPostAndLiker(Discuss post, User liker);

    long countByPost(Discuss post);

    void deleteByPostAndLiker(Discuss post, User liker);
}
