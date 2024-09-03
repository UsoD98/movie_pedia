package com.pedia.movie.user.dto;

import com.pedia.movie.user.entity.User;
import lombok.Data;
import lombok.Setter;

@Data
public class UserResponse {

    private long id;
    private String name;
    private String email;
    private int followers;
    private int followings;
    private int ratingsCount;
    private int commentsCount;
    private int wishCount;
    private int watchingCount;

    private boolean isFollowing;

    public static UserResponse from(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setFollowers(user.getFollowers());
        userResponse.setFollowings(user.getFollowings());
        userResponse.setRatingsCount(user.getRatingsCount());
        userResponse.setCommentsCount(user.getCommentsCount());
        userResponse.setWishCount(user.getWishCount());
        userResponse.setWatchingCount(user.getWatchingCount());

        return userResponse;
    }

    public void setIsFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

}
