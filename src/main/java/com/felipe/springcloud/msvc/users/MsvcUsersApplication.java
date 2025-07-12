package com.felipe.springcloud.msvc.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.felipe.springcloud.msvc.users.entities.Role;
import com.felipe.springcloud.msvc.users.entities.User;
import com.felipe.springcloud.msvc.users.repositories.RoleRepository;
import com.felipe.springcloud.msvc.users.repositories.UserRepository;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
public class MsvcUsersApplication {

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Component
	class DataInitializer implements CommandLineRunner {

		private final UserRepository userRepository;
		private final RoleRepository roleRepository;
		private final PasswordEncoder passwordEncoder;

		public DataInitializer(UserRepository userRepository, RoleRepository roleRepository,
				PasswordEncoder passwordEncoder) {
			this.userRepository = userRepository;
			this.roleRepository = roleRepository;
			this.passwordEncoder = passwordEncoder;
		}

		@Override
		public void run(String... args) throws Exception {
			// Create roles if they don't exist
			if (roleRepository.count() == 0) {
				Role userRole = new Role();
				userRole.setName("ROLE_USER");
				roleRepository.save(userRole);

				Role adminRole = new Role();
				adminRole.setName("ROLE_ADMIN");
				roleRepository.save(adminRole);
			}

			// Create users if they don't exist
			if (userRepository.count() == 0) {
				Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow();
				Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();

				// Create felipe user
				User felipe = new User();
				felipe.setUsername("felipe");
				felipe.setPassword(passwordEncoder.encode("123456"));
				felipe.setEmail("felipe@test.com");
				felipe.setEnabled(true);
				felipe.setRoles(Arrays.asList(userRole));
				userRepository.save(felipe);

				// Create admin user
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("123456"));
				admin.setEmail("admin@test.com");
				admin.setEnabled(true);
				admin.setRoles(Arrays.asList(userRole, adminRole));
				userRepository.save(admin);

				System.out.println("=== Users created successfully ===");
				System.out.println("Username: felipe, Password: 123456");
				System.out.println("Username: admin, Password: 123456");
				System.out.println("================================");
			}
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(MsvcUsersApplication.class, args);
	}

}
