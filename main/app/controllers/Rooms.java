package controllers;

import play.mvc.With;

/**
 * Extend index so we can get the CRUD functionalities for Room table */
@With(Secure.class)
public class Rooms extends Index { }