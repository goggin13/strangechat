package controllers;
import play.mvc.With;

/**
 * Extend index so we can get the CRUD functionalities for Server table */
@With(Secure.class)
public class Servers extends Index { }