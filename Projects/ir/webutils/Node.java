package ir.webutils;

import java.util.*;
import java.io.*;

/**
 * Node in the the Graph data structure.
 *
 * @see Graph
 *
 * @author Ray Mooney
 */
public class Node {

  /**
   * Name of the node.
   */
  String name;

  /**
   * Lists of incoming and outgoing edges.
   */
  Set<Node> edgesOut = new HashSet<>();
  Set<Node> edgesIn = new HashSet<>();

  /**
   * Constructs a node with that name.
   */
  public Node(String name) {
    this.name = name;
  }

  /**
   * Adds an outgoing edge
   */
  public void addEdge(Node node) {
    edgesOut.add(node);
    node.addEdgeFrom(this);
  }

  /**
   * Adds an incoming edge
   */
  void addEdgeFrom(Node node) {
    edgesIn.add(node);
  }

  /**
   * Returns the name of the node
   */
  public String toString() {
    return name;
  }

  /**
   * Gives the list of outgoing edges
   */
  public Set<Node> getEdgesOut() {
    return edgesOut;
  }

  /**
   * Gives the list of incoming edges
   */
  public Set<Node> getEdgesIn() {
    return edgesIn;
  }

  public boolean equals(Object other) {
    return name.equals(((Node) other).name);
  }

  public int hashCode() {
    return name.hashCode();
  }
}
