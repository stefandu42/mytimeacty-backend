package mytimeacty.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mytimeacty.annotation.RolesAllowed;
import mytimeacty.model.users.dto.UserDetailsDTO;
import mytimeacty.model.users.dto.UserProfileDTO;
import mytimeacty.service.UserService;
import mytimeacty.utils.SecurityUtils;


@RestController
@RequestMapping("/users")
public class UserController {
	
    private UserService userService;
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    

	public UserController(UserService userService) {
	    this.userService = userService;
	}
	
	/**
     * Retrieves the profile of a specific user by their ID.
     * 
     * @param userId the ID of the user whose profile is being retrieved
     * @return a ResponseEntity containing the UserProfileDTO and HTTP status 200 OK
     */
	@GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable int userId) {
        UserProfileDTO userProfileDTO = userService.getUserProfile(userId);
        logger.info("User with the nickname '{}' has successfully retrieved the profile of user with id '{}'", 
        		SecurityUtils.getCurrentUser().getNickname(), userId);
        return ResponseEntity.status(HttpStatus.OK).body(userProfileDTO);
    }
    
	/**
     * Retrieves a paginated list of users filtered by their nickname.
     * 
     * @param nickname the nickname to filter users by
     * @param page the page number to retrieve (zero-based)
     * @param size the number of items per page
     * @return a ResponseEntity containing a Page of UserDetailsDTO and HTTP status 200 OK
     */
	@GetMapping
    public ResponseEntity<Page<UserDetailsDTO>> getFilteredUsers(
            @RequestParam String nickname, 
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "15") int size) {
        
        Page<UserDetailsDTO> userDetailsDTOs = userService.getFilteredUsers(nickname, page, size);
        logger.info("User with the nickname '{}' has successfully retrieved all users using params nickname '{}', "
        		+ "page '{}' and size '{}'", 
        		SecurityUtils.getCurrentUser().getNickname(), nickname, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(userDetailsDTOs);
    }
			
	/**
     * Bans a specific user. Only accessible by users with the 'admin' or 'chief' roles.
     * 
     * @param userId the ID of the user to be banned
     * @return a ResponseEntity with a success message and HTTP status 200 OK
     */
	@RolesAllowed({"admin", "chief"})
    @PutMapping("/{userId}/ban")
    public ResponseEntity<String> banUser(@PathVariable int userId) {
		userService.banUser(userId);

		logger.info("User with the nickname '{}' (role: '{}') has successfully marked the user with id '{}' as banned", 
        		SecurityUtils.getCurrentUser().getNickname(), SecurityUtils.getCurrentUser().getUserRole(), userId);
        return ResponseEntity.status(HttpStatus.OK).body("User banned successfully");
    }
	
	/**
     * Unbans a specific user. Only accessible by users with the 'admin' or 'chief' roles.
     * 
     * @param userId the ID of the user to be unbanned
     * @return a ResponseEntity with a success message and HTTP status 200 OK
     */
	@RolesAllowed({"admin", "chief"})
    @PutMapping("/{userId}/unban")
    public ResponseEntity<String> unbanUser(@PathVariable int userId) {
		userService.unbanUser(userId);

		logger.info("User with the nickname '{}' (role: '{}') has successfully marked the user with id '{}' as un-banned and"
				+ " restored his previous role", 
        		SecurityUtils.getCurrentUser().getNickname(), SecurityUtils.getCurrentUser().getUserRole(), userId);
        return ResponseEntity.status(HttpStatus.OK).body("User unbanned successfully");
    }
	
	/**
     * Promotes a user to the 'admin' role. Only accessible by users with the 'chief' role.
     * 
     * @param userId the ID of the user to be promoted
     * @return a ResponseEntity with a success message and HTTP status 200 OK
     */
	@RolesAllowed({"chief"})
    @PutMapping("/{userId}/promote-to-admin")
    public ResponseEntity<String> promoteUserToAdmin(@PathVariable int userId) {
		userService.promoteUserToAdmin(userId);
		logger.info("Chief with the nickname '{}' has successfully promoted the user with id '{}' as admin", 
        		SecurityUtils.getCurrentUser().getNickname(), userId);
        return ResponseEntity.status(HttpStatus.OK).body("User promoted to admin successfully");
    }

	/**
     * Promotes an 'admin' user to the 'chief' role. Only accessible by users with the 'chief' role.
     * 
     * @param userId the ID of the admin to be promoted
     * @return a ResponseEntity with a success message and HTTP status 200 OK
     */
	@RolesAllowed({"chief"})
    @PutMapping("/{userId}/promote-to-chief")
    public ResponseEntity<String> promoteAdminToChief(@PathVariable int userId) {
		userService.promoteAdminToChief(userId);
		logger.info("Chief with the nickname '{}' has successfully promoted the admin with id '{}' as chief", 
        		SecurityUtils.getCurrentUser().getNickname(), userId);
        return ResponseEntity.status(HttpStatus.OK).body("Admin promoted to chief successfully");
    }

	/**
     * Demotes an 'admin' user to a regular user. Only accessible by users with the 'chief' role.
     * 
     * @param userId the ID of the admin to be demoted
     * @return a ResponseEntity with a success message and HTTP status 200 OK
     */
	@RolesAllowed({"chief"})
    @PutMapping("/{userId}/demote-to-user")
    public ResponseEntity<String> demoteAdminToUser(@PathVariable int userId) {
		userService.demoteAdminToUser(userId);
		logger.info("Chief with the nickname '{}' has successfully demoted the admin with id '{}' as user", 
        		SecurityUtils.getCurrentUser().getNickname(), userId);
        return ResponseEntity.status(HttpStatus.OK).body("Admin demoted to user successfully");
    }
    
}