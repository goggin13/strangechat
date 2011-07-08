package controllers;
import play.*;
import play.mvc.*;

/**
 * Extend index so we can get the CRUD functionalities for Server table */
@With(Secure.class)
public class Servers extends Index { }