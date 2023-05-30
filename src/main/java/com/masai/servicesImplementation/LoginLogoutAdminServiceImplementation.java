
package com.masai.servicesImplementation;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.masai.exceptions.AdminException;
import com.masai.exceptions.LoginException;
import com.masai.exceptions.LogoutException;
import com.masai.exceptions.UserException;
import com.masai.model.Admin;
import com.masai.model.CurrentAdminSession;
import com.masai.model.User;
import com.masai.repository.AdminRepo;
import com.masai.repository.CurrentAdminSessionRepo;
import com.masai.services.LoginLogoutAdminService;

import net.bytebuddy.utility.RandomString;

/**
 * @author mirkhamidov
 *
 */

@Service
public class LoginLogoutAdminServiceImplementation implements LoginLogoutAdminService {

	@Autowired
	private AdminRepo adminRepo;

	@Autowired
	private CurrentAdminSessionRepo currentAdminSessionRepo;

	@Override
	public String logoutAdmin(String key) throws LogoutException {

		Optional<CurrentAdminSession> optional_currentAdminSession = currentAdminSessionRepo.findByKey(key);

		if (optional_currentAdminSession.isPresent()) {

			currentAdminSessionRepo.delete(optional_currentAdminSession.get());

			return "Вы вышли из системы !";

		} else {
			throw new LogoutException("Нет пользователя, вошедшего в систему !");

		}
	}

	@Override
	public User authenticateAdmin(User user, String key) throws UserException, AdminException, LoginException {

		Optional<CurrentAdminSession> optional_currentAdminSession = currentAdminSessionRepo.findByKey(key);

		if (optional_currentAdminSession.isPresent()) {

			CurrentAdminSession currentAdminSession = optional_currentAdminSession.get();

			Optional<Admin> optional_admin = adminRepo.findById(currentAdminSession.getAdminId());

			if (optional_admin.isPresent()) {

				Admin admin = optional_admin.get();

				if (admin.getMobileNumber().equals(user.getId()) && admin.getPassword().equals(user.getPassword())) {

					return user;
				} else {
					throw new UserException("Неверный идентификатор пользователя или пароль");
				}

			} else {
				throw new AdminException(
						"Зарегистрированный администратор с этим идентификатором администратора не найден : " + currentAdminSession.getAdminId());
			}

		} else {
			throw new LoginException("Неверный ключ администратора !");
		}

	}

	@Override
	public Admin validateAdmin(String key) throws AdminException, LoginException {

		Optional<CurrentAdminSession> optional_currentAdminSession = currentAdminSessionRepo.findByKey(key);

		if (optional_currentAdminSession.isPresent()) {

			CurrentAdminSession currentAdminSession = optional_currentAdminSession.get();

			Optional<Admin> optional_admin = adminRepo.findById(currentAdminSession.getAdminId());

			if (optional_admin.isPresent()) {

				Admin admin = optional_admin.get();

				return admin;

			} else {
				throw new AdminException(
						"Зарегистрированный администратор с этим идентификатором администратора не найден : " + currentAdminSession.getAdminId());
			}

		} else {
			throw new LoginException("Неверный ключ администратора !");
		}

	}

	@Override
	public CurrentAdminSession loginAdmin(User user) throws LoginException, AdminException {

		if ("Admin".equals(user.getRole())) {

			Optional<Admin> optionalAdmin = adminRepo.findByMobileNumber(user.getId());

			if (optionalAdmin.isPresent()) {

				Admin admin = optionalAdmin.get();

				Optional<CurrentAdminSession> optional_currentAdminSession = currentAdminSessionRepo
						.findByAdminId(admin.getAdminId());

				if (optional_currentAdminSession.isEmpty()) {

					CurrentAdminSession currentAdminSession = new CurrentAdminSession();

					String key = RandomString.make(6);

					currentAdminSession.setAdminId(admin.getAdminId());
					currentAdminSession.setLocalDateTime(LocalDateTime.now());
					currentAdminSession.setKey(key);

					return currentAdminSessionRepo.save(currentAdminSession);
				} else {
					throw new LoginException("Данные пользователь уже авторизован под ID : " + admin.getAdminId());
				}

			} else {
				throw new AdminException("Зарегистрированный администратор с этим идентификатором пользователя не найден: " + user.getId());
			}

		} else {
			throw new LoginException("Пожалуйста, выберите Admin в качестве роли для входа!");
		}

	}

}
