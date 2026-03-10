package com.ubo.tp.message.core;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.ubo.tp.message.core.database.EntityManager;
import com.ubo.tp.message.core.database.IDatabase;
import com.ubo.tp.message.core.database.IDatabaseObserver;
import com.ubo.tp.message.core.directory.IWatchableDirectory;
import com.ubo.tp.message.core.directory.WatchableDirectory;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.IMessageRecipient;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;

/**
 * Classe permettant de manipuler les données de l'application.
 *
 * @author S.Lucas
 */
public class DataManager {

	/**
	 * Base de donnée de l'application.
	 */
	protected final IDatabase mDatabase;

	/**
	 * Gestionnaire des entités contenu de la base de données.
	 */
	protected final EntityManager mEntityManager;

	/**
	 * Classe de surveillance de répertoire
	 */
	protected IWatchableDirectory mWatchableDirectory;

	/**
	 * Constructeur.
	 */
	public DataManager(IDatabase database, EntityManager entityManager) {
		mDatabase = database;
		mEntityManager = entityManager;
	}

	/**
	 * Ajoute un observateur sur les modifications de la base de données.
	 *
	 * @param observer
	 */
	protected void addObserver(IDatabaseObserver observer) {
		this.mDatabase.addObserver(observer);
	}

	/**
	 * Supprime un observateur sur les modifications de la base de données.
	 *
	 * @param observer
	 */
	protected void removeObserver(IDatabaseObserver observer) {
		this.mDatabase.removeObserver(observer);
	}

	/**
	 * Retourne la liste des Utilisateurs.
	 */
	public Set<User> getUsers() {
		return this.mDatabase.getUsers();
	}

	/**
	 * Retourne la liste des Messages.
	 */
	public Set<Message> getMessages() {
		return this.mDatabase.getMessages();
	}

	/**
	 * Retourne la liste des Canaux.
	 */
	public Set<Channel> getChannels() {
		return this.mDatabase.getChannels();
	}

	/**
	 * Ecrit un message.
	 *
	 * @param message
	 */
	public void sendMessage(Message message) {
		// Ecrit un message
		this.mEntityManager.writeMessageFile(message);
	}

	/**
	 * Ecrit un Utilisateur.
	 *
	 * @param user
	 */
	public void sendUser(User user) {
		// Ecrit un utilisateur
		this.mEntityManager.writeUserFile(user);
	}

	/**
	 * Ecrit un Canal.
	 *
	 * @param channel
	 */
	public void sendChannel(Channel channel) {
		// Ecrit un canal
		this.mEntityManager.writeChannelFile(channel);
	}

	/**
	 * Retourne tous les Messages d'un utilisateur.
	 *
	 * @param user utilisateur dont les messages sont à rechercher.
	 */
	public Set<Message> getMessagesFrom(User user) {
		Set<Message> userMessages = new HashSet<>();

		// Parcours de tous les messages de la base
		for (Message message : this.getMessages()) {
			// Si le message est celui recherché
			if (message.getSender().equals(user)) {
				userMessages.add(message);
			}
		}

		return userMessages;
	}

	/**
	 * Retourne tous les Messages d'un utilisateur addressé à un autre.
	 *
	 * @param user utilisateur dont les messages sont à rechercher.
	 * @param user destinataire des messages recherchés.
	 */
	public Set<Message> getMessagesFrom(User sender, IMessageRecipient recipient) {
		Set<Message> userMessages = new HashSet<>();

		// Parcours de tous les messages de l'utilisateur
		for (Message message : this.getMessagesFrom(sender)) {
			// Si le message est celui recherché
			if (message.getRecipient().equals(recipient.getUuid())) {
				userMessages.add(message);
			}
		}

		return userMessages;
	}

	/**
	 * Retourne tous les Messages adressés à un utilisateur.
	 *
	 * @param user utilisateur dont les messages sont à rechercher.
	 */
	public Set<Message> getMessagesTo(User user) {
		Set<Message> userMessages = new HashSet<>();

		// Parcours de tous les messages de la base
		for (Message message : this.getMessages()) {
			// Si le message est celui recherché
			if (message.getSender().equals(user)) {
				userMessages.add(message);
			}
		}

		return userMessages;
	}

	/**
	 * Assignation du répertoire d'échange.
	 * 
	 * @param directoryPath
	 */
	public void setExchangeDirectory(String directoryPath) {
		mEntityManager.setExchangeDirectory(directoryPath);

		mWatchableDirectory = new WatchableDirectory(directoryPath);
		mWatchableDirectory.initWatching();
		mWatchableDirectory.addObserver(mEntityManager);
	}

	public void deleteUser(User user) {
		if (user == null) {
			System.err.println("Utilisateur invalide.");
			return;
		}

		// Générer le chemin du fichier utilisateur
		String userFilePath = mEntityManager.getUserFilePath(user);
		if (userFilePath == null) {
			System.err.println("Impossible de générer le chemin du fichier utilisateur.");
			return;
		}

		File userFile = new File(userFilePath);
		if (userFile.exists()) {
			if (userFile.delete()) {
				System.out.println("Fichier utilisateur supprimé : " + userFile.getAbsolutePath());
			} else {
				System.err.println("Échec de la suppression du fichier utilisateur : " + userFile.getAbsolutePath());
			}
		} else {
			System.err.println("Fichier utilisateur introuvable : " + userFile.getAbsolutePath());
		}
	}	

	public void deleteMessage(Message message, User currentUser) {
		if (message != null && currentUser != null && 
			message.getSender().getUuid().equals(currentUser.getUuid())) {
			
			String path = mEntityManager.getMessageFilePath(message);
			if (path != null) {
				new File(path).delete();
			}
		}
	}
}
