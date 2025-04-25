/*
 * Copyright (c) 2022-2022 the original author or authors.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.example.psoft_22_23_project.bootstrapping;

import com.example.psoft_22_23_project.usermanagement.model.Role;
import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.usermanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Profile("bootstrap")
public class UserBootstrapper implements CommandLineRunner {

	private final UserRepository userRepo;

	private final PasswordEncoder encoder;

	@Override
	@Transactional
	public void run(final String... args) throws Exception {
		// admin
		if (userRepo.findByUsername("admin@mail.com").isEmpty()) {
			final User u1 = new User("admin@mail.com", encoder.encode("adminpass"), "admin@mail.com", 123456789, 35);
			u1.addAuthority(new Role(Role.User_Admin));
			userRepo.save(u1);
		}

		// Marketing Director
		if (userRepo.findByUsername("chico@mail.com").isEmpty()) {
			final User marketing = new User("chico@mail.com", encoder.encode("chicopass"), "chico@mail.com", 987654321, 42);
			marketing.addAuthority(new Role(Role.Marketing_Director));
			userRepo.save(marketing);
		}

		//Subs1 - Tomas
		if (userRepo.findByUsername("tomas@mail.com").isEmpty()) {
			final User u2 = new User("tomas@mail.com", encoder.encode("tomaspass"), "tomas@mail.com", 555123456, 28);
			u2.addAuthority(new Role(Role.Subscriber));
			u2.setLocation("porto");
			userRepo.save(u2);
		}
		//Subs1 - Alex
		if (userRepo.findByUsername("alex@mail.com").isEmpty()) {
			final User u2 = new User("alex@mail.com", encoder.encode("alexpass"), "alex@mail.com", 555987654, 24);
			u2.addAuthority(new Role(Role.Subscriber));
			userRepo.save(u2);
		}
		//Product1 - Martim
		if (userRepo.findByUsername("martim@mail.com").isEmpty()) {
			final User u2 = new User("martim@mail.com", encoder.encode("martimpass"), "martim@mail.com", 555111222, 31);
			u2.addAuthority(new Role(Role.Project_Manager));
			userRepo.save(u2);
		}
		//Product2 - Martim
		if (userRepo.findByUsername("martim2@mail.com").isEmpty()) {
			final User u2 = new User("martim2@mail.com", encoder.encode("martimpass2"), "martim2@mail.com", 555333444, 33);
			u2.addAuthority(new Role(Role.Financial_director));
			userRepo.save(u2);
		}
	}
}
