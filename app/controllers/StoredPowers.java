package controllers;
import play.mvc.With;

/**
 * Extend index so we can get the CRUD functionalities for StoredPowers table */
@With(Secure.class)
public class StoredPowers extends Index { }