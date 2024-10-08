package mytimeacty.model.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileDTO {
    private Integer userId;
    private String nickname;
    private String email;
    private Integer followersCount;
    private Integer followingCount;
    private Integer createdQuizzesCount;
    private Integer likedQuizzCount;
    private Boolean isFollowing;
}