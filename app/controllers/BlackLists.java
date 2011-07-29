package controllers;
import play.*;
import play.mvc.*;

/**
 * Extend index so we can get the CRUD functionalities for Room table */
@With(Secure.class)
public class BlackLists extends Index { }