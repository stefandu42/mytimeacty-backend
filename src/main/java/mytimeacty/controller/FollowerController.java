package mytimeacty.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mytimeacty.model.followers.dto.FollowerDTO;
import mytimeacty.model.followers.dto.FollowingDTO;
import mytimeacty.service.FollowerService;
import mytimeacty.utils.SecurityUtils;

@RestController
@RequestMapping("/followers")
public class FollowerController {

    @Autowired
    private FollowerService followerService;
    
    private static final Logger logger = LoggerFactory.getLogger(FollowerController.class);
    
    /**
     * Retrieves a paginated list of followers for a specified user.
     * 
     * This endpoint returns a paginated list of followers for the user identified by the provided user ID.
     * The pagination can be customized using the `page` and `size` query parameters.
     * 
     * @param userId the ID of the user whose followers are to be retrieved.
     * @param page the page number to retrieve (zero-based).
     * @param size the number of items per page.
     * @return a ResponseEntity containing a Page of FollowerDTO objects with the followers of the user.
     */
    @GetMapping("/users/{userId}/followers")
    public ResponseEntity<Page<FollowerDTO>> getFollowers(
            @PathVariable int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        Page<FollowerDTO> followers = followerService.getFollowersByUserId(userId, page, size);
        logger.info("User with the nickname '{}' has successfully retrieved all followers of user with id '{}'", 
        		SecurityUtils.getCurrentUser().getNickname(), userId);
        return ResponseEntity.status(HttpStatus.OK).body(followers);
    }

    /**
     * Retrieves a paginated list of followings for a specified user.
     * 
     * This endpoint returns a paginated list of users that the user identified by the provided user ID is following.
     * The pagination can be customized using the `page` and `size` query parameters.
     * 
     * @param userId the ID of the user whose followings are to be retrieved.
     * @param page the page number to retrieve (zero-based).
     * @param size the number of items per page.
     * @return a ResponseEntity containing a Page of FollowingDTO objects with the users followed by the user.
     */
    @GetMapping("/users/{userId}/followings")
    public ResponseEntity<Page<FollowingDTO>> getFollowings(
            @PathVariable int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        Page<FollowingDTO> followings = followerService.getFollowingsByUserId(userId, page, size);
        logger.info("User with the nickname '{}' has successfully retrieved all followings of user with id '{}'", 
        		SecurityUtils.getCurrentUser().getNickname(), userId);
        return ResponseEntity.status(HttpStatus.OK).body(followings);
    }

    /**
     * Allows the current user to follow another user.
     * 
     * This endpoint enables the currently authenticated user to follow a user identified by the provided user ID.
     * If the user attempts to follow themselves, a bad request response is returned.
     * 
     * @param idUserFollowed the ID of the user to be followed.
     * @return a ResponseEntity with a status of 201 Created if the follow action is successful
     */
    @PostMapping("/follow/{idUserFollowed}")
    public ResponseEntity<String> followUser(@PathVariable int idUserFollowed) {
    	followerService.followUser(SecurityUtils.getCurrentUser().getIdUser(), idUserFollowed);
    	logger.info("User with the nickname '{}' has successfully follow the user with id '{}'", 
        		SecurityUtils.getCurrentUser().getNickname(), idUserFollowed);
        return ResponseEntity.status(HttpStatus.CREATED).build(); 
    }
    
    /**
     * Allows the current user to unfollow another user.
     * 
     * This endpoint enables the currently authenticated user to unfollow a user identified by the provided user ID.
     * 
     * @param idUserFollowed the ID of the user to be unfollowed.
     * @return a ResponseEntity with a status of 204 No Content if the unfollow action is successful.
     */
    @DeleteMapping("/unfollow/{idUserFollowed}")
    public ResponseEntity<String> unfollowUser(@PathVariable int idUserFollowed) {
        followerService.unfollowUser(SecurityUtils.getCurrentUser().getIdUser(), idUserFollowed);
        logger.info("User with the nickname '{}' has successfully unfollow the user with id '{}'", 
        		SecurityUtils.getCurrentUser().getNickname(), idUserFollowed);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); 
    }
}