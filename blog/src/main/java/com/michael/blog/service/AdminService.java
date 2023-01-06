package com.michael.blog.service;

public interface AdminService {

    String deleteUser(Long userId);

    String activationUserProfile(Long userId);

    String deactivationUserProfile(Long userId);

    String deletePost(Long postId);

    String deleteComment(Long commentId);

    String changeUserRoleToAdmin(Long userId);

    String changeAdminRoleToUser(Long userId);


}
