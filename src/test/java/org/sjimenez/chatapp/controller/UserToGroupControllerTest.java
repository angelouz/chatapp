package org.sjimenez.chatapp.controller;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.sjimenez.chatapp.controllers.UserToGroupController;
import org.sjimenez.chatapp.delegate.GroupDelegate;
import org.sjimenez.chatapp.model.Group;
import org.sjimenez.chatapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserToGroupControllerTest {
	
	@Autowired
    private TestRestTemplate restTemplate;
	
	@Autowired
	private UserToGroupController userToGroupController;
	
	@MockBean
	private GroupDelegate groupDelegate;
	
    @LocalServerPort
    private int port;
	
	private Group testGroupBean;
	
	private List<User> userList;
	
	private User user;
	
	private String URL;
	
	private HttpEntity<Void> request;
	
	@Before
	public void init() {
		testGroupBean = createGroupForTest("GroupTesting1");
		
		userList = new ArrayList<User>();
		
		userList.add(createUserForTest());
		userList.add(createUserForTest());
		userList.add(createUserForTest());		
		
		URL = "http://localhost:" + String.valueOf(port) + "/userToGroup/";
		
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        request = new HttpEntity<Void>(headers);
	}
	
	@Test
	public void fetchUsersByGroupName()
	{
		when(groupDelegate.fetchUsersByGroupName(testGroupBean.getGroupName())).thenReturn(userList);
		
		//ResponseEntity<List<User>> responseEntity =  userToGroupController.fetchUsersByGroupName(testGroupBean.getGroupName());
		ResponseEntity<Group> responseEntity = restTemplate
				.exchange(URL+testGroupBean.getGroupName(), HttpMethod.POST, request, Group.class);
		
		verify(groupDelegate, times(1)).fetchUsersByGroupName(testGroupBean.getGroupName());
		assertArrayEquals(userList, responseEntity.getBody());
	}
	
	@Test
	public void addUserToGroup()
	{
		List<Integer> newUsers = new ArrayList<Integer>();
		user = createUserForTest();
		newUsers.add(user.getIduser() );
		userList.add(user);
		
		when(groupDelegate.addUserToGroup(testGroupBean.getGroupName(), newUsers)).thenReturn(userList);
		ResponseEntity<List<User>> responseEntity =  userToGroupController.addUserToGroup(testGroupBean.getGroupName(), newUsers);
		
		verify(groupDelegate).addUserToGroup(testGroupBean.getGroupName(), newUsers);
		assertArrayEquals(userList.toArray(), responseEntity.getBody().toArray());
	}
	
	@Test
	public void removerUserFromGroup()
	{
		List<Integer> removeUsers = new ArrayList<Integer>();
		user = userList.get(userList.size()-1 );
		removeUsers.add(user.getIduser() );
		userList.remove(user);
		
		when(groupDelegate.removeUserFromGroup(testGroupBean.getGroupName(), removeUsers)).thenReturn(userList);
		ResponseEntity<List<User>> responseEntity =  userToGroupController.removeUserFromGroup(testGroupBean.getGroupName(), removeUsers);
		
		verify(groupDelegate).removeUserFromGroup(testGroupBean.getGroupName(), removeUsers);
		assertArrayEquals(userList.toArray(), responseEntity.getBody().toArray());
	}
	
	
	private Group createGroupForTest(String groupName) {
		Group group = new Group();
		group.setGroupName(groupName);
		group.setCreatedDate(LocalDate.now());
		group.setGroupId(1);

		return group;
	}
	
	private User createUserForTest()
	{
		LocalTime localtime = LocalTime.now();
		int randomNum = ThreadLocalRandom.current().nextInt(1, 15000 + 1);
			
		User user = new User();
		user.setBirthdate(LocalDate.now());
		user.setIduser(randomNum);
		user.setLastName("lastName");
		user.setMail(localtime.toString() + "@hotmail.com");
		user.setName("name");
		user.setNickname(localtime.toString());
		
		return user;
	}
}
