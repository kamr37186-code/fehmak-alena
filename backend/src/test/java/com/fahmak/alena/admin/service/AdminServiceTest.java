package com.fahmak.alena.admin.service;

import com.fahmak.alena.admin.dto.AdminDashboardResponse;
import com.fahmak.alena.user.entity.Role;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDashboardData_ShouldReturnCorrectData() {
        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("Alice");
        user1.setLastName("Smith");
        user1.setEmail("alice@test.com");
        user1.setRole(Role.STUDENT);
        user1.setStatus("ACTIVE");

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Bob");
        user2.setLastName("Jones");
        user2.setEmail("bob@test.com");
        user2.setRole(Role.INSTRUCTOR);
        user2.setStatus("INACTIVE");

        when(userService.findAll()).thenReturn(List.of(user1, user2));

        AdminDashboardResponse response = adminService.getDashboardData();

        assertNotNull(response);
        assertEquals(2, response.getTotalUsers());
        assertEquals(2, response.getUsers().size());
        assertNotNull(response.getSystemStats());
        assertEquals("USR-0001", response.getUsers().get(0).getId());
        assertEquals("Alice Smith", response.getUsers().get(0).getName());
        assertEquals("ACTIVE", response.getUsers().get(0).getStatus());

        verify(userService).findAll();
    }
}
