package org.sjimenez.chatapp.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.sjimenez.chatapp.controllers.GroupController;
import org.sjimenez.chatapp.delegate.GroupDelegate;
import org.sjimenez.chatapp.mappers.UserMapper;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GroupControllerTest {

	@Autowired
	private GroupController groupController;
	
	@MockBean
	private GroupDelegate groupDelegate;

    @Autowired
    private TestRestTemplate restTemplate;
	
    @MockBean
    private UserMapper userMapper;
    
    @LocalServerPort
    private int port;
    
	private Group testGroupBean;
	
	private String URL;
	
	private HttpEntity<Void> request;
	
	@Before
	public void init() {
		testGroupBean = createGroupForTest("GroupTesting1");
		
		URL = "http://localhost:" + String.valueOf(port) + "/group/";
		
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        request = new HttpEntity<Void>(headers);
	}
	
	@Test
	public void createGroup() {
		when(groupDelegate.createGroup(testGroupBean.getGroupName())).thenReturn(testGroupBean);
		
		ResponseEntity<Group> responseEntity = restTemplate
				.exchange(URL+testGroupBean.getGroupName(), HttpMethod.POST, request, Group.class);

		verify(groupDelegate).createGroup(testGroupBean.getGroupName());
		assertThat( responseEntity.getStatusCode(), is(HttpStatus.OK) );
		assertThat( responseEntity.getBody().getGroupName(), is(testGroupBean.getGroupName( )) );
		assertThat( responseEntity.getBody().getGroupId(), is(testGroupBean.getGroupId()) );
		assertThat( responseEntity.getBody().getCreatedDate(), is(testGroupBean.getCreatedDate()) );
	}

	@Test
	public void fetchGroupByName() {
		when(groupDelegate.fetchGroupByName(testGroupBean.getGroupName())).thenReturn(testGroupBean);
		
		ResponseEntity<Group> responseEntity = restTemplate
				.exchange(URL+testGroupBean.getGroupName(), HttpMethod.GET, request, Group.class);
		
		verify(groupDelegate).fetchGroupByName(testGroupBean.getGroupName());
		assertThat( responseEntity.getStatusCode(), is(HttpStatus.OK) );
		assertThat( responseEntity.getBody().getGroupName(), is(testGroupBean.getGroupName( )) );
		assertThat( responseEntity.getBody().getGroupId(), is(testGroupBean.getGroupId()) );
		assertThat( responseEntity.getBody().getCreatedDate(), is(testGroupBean.getCreatedDate()) );
	}
	
	@Test
	public void updateGroupByName() {
		Group groupRenamed = new Group();
		groupRenamed.setGroupName(testGroupBean.getGroupName()+"2");
		groupRenamed.setCreatedDate(testGroupBean.getCreatedDate());
		groupRenamed.setGroupId(testGroupBean.getGroupId());
		
		testGroupBean.setGroupName(testGroupBean.getGroupName());
		when(groupDelegate.updateGroupByName(testGroupBean.getGroupName(), groupRenamed.getGroupName())).thenReturn(testGroupBean);
		
		//TODO: Fix the problem with the http call to send the query param
		ResponseEntity<Group> responseEntity =  groupController.updateGroupByName(testGroupBean.getGroupName(), groupRenamed.getGroupName());
		//	restTemplate	.exchange(URL+testGroupBean.getGroupName(), HttpMethod.PUT, request, Group.class);
		
				
		verify(groupDelegate).updateGroupByName(testGroupBean.getGroupName(), groupRenamed.getGroupName());
		assertThat( responseEntity.getStatusCode(), is(HttpStatus.OK) );
		assertThat( responseEntity.getBody().getGroupName(), is(testGroupBean.getGroupName( )) );
		assertThat( responseEntity.getBody().getGroupId(), is(testGroupBean.getGroupId()) );
		assertThat( responseEntity.getBody().getCreatedDate(), is(testGroupBean.getCreatedDate()) );
	}

	@Test
	public void deleteGroupByName() {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Group> responseEntity = restTemplate
		.exchange(URL+testGroupBean.getGroupName(), HttpMethod.DELETE, request, Group.class);
		
		verify(groupDelegate, times(1)).deleteGroupByName(testGroupBean.getGroupName());
		assertThat( responseEntity.getStatusCode(), is(HttpStatus.OK) );
	}

	private Group createGroupForTest(String groupName) {
		Group group = new Group();
		group.setGroupName(groupName);
		group.setCreatedDate(LocalDate.now());
		group.setGroupId(1);

		return group;
	}
}
