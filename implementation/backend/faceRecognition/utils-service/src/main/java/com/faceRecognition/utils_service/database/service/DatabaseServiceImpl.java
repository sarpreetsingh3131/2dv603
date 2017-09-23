package com.faceRecognition.utils_service.database.service;

import javax.naming.directory.InvalidAttributeValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.faceRecognition.utils_service.database.model.Credentials;
import com.faceRecognition.utils_service.database.model.UserEntity;
import com.faceRecognition.utils_service.database.repository.CredentialsRepository;
import com.faceRecognition.utils_service.database.repository.UserEntitiesRepository;

@Service
public class DatabaseServiceImpl implements AdminDBService, UserDBService, AuthenticationService {

	@Autowired
	private UserEntitiesRepository userEntitiesRepository;

	@Autowired
	private CredentialsRepository credentialsRepository;

	@Override
	public String getPersonalNumber(String id) throws NotFoundException {
		UserEntity entity = this.userEntitiesRepository.findOne(new Long(id));
		if (entity == null)
			throw new NotFoundException();
		return entity.getPersonalNumber();
	}

	@Override // PN Format = YYYYMMDDXXXX
	public UserEntity addUserEntity(String url, String personalNumber) throws InvalidAttributeValueException {
		if (personalNumber.length() != 12 || !personalNumber.matches("\\d+"))
			throw new InvalidAttributeValueException();
		return this.userEntitiesRepository.saveAndFlush(new UserEntity(personalNumber, url));
	}

	@Override
	public UserEntity updateUserEntity(String id, String personalNumber, String url) throws NotFoundException {
		UserEntity entity = this.userEntitiesRepository.findOne(new Long(id));
		if (entity == null)
			throw new NotFoundException();
		if (!personalNumber.isEmpty() && personalNumber != null) {
			entity.setPersonalNumber(personalNumber);
			entity.setPhotoLink(url);
			return this.userEntitiesRepository.saveAndFlush(entity);
		} else
			throw new NotFoundException();
	}

	@Override
	public void deleteUserEntity(String id) throws NotFoundException {
		UserEntity entity = this.userEntitiesRepository.findOne(new Long(id));
		if (entity == null)
			throw new NotFoundException();
		this.userEntitiesRepository.delete(entity);
	}

	@Override
	public UserEntity getUserEntity(String id) throws NotFoundException {
		UserEntity entity = this.userEntitiesRepository.findOne(new Long(id));
		if (entity == null)
			throw new NotFoundException();
		return entity;
	}

	@Override
	public boolean isValidCredentials(String username, String password) {
		Credentials credentials = this.credentialsRepository.findOne(username);
		return credentials == null ? false : credentials.getPassword().equals(password);
	}

	@Override
	public Page<UserEntity> getUserEntities(PageRequest request) {
		Page<UserEntity> entites = this.userEntitiesRepository.findAll(request);
		return entites;
	}
}