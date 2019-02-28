/**
  *
  * Beschreibung
  *
  * @version 1.0 vom 08.12.2010
  * @author
  */

public class List {

  // Anfang Attribute
  private Node first;
  private Node last;
  private Node current;
  // Ende Attribute

  // Anfang Methoden
  
  private class Node {
    private Object content;
    private Node next;
    public Node() { next = null; content = null; }
    public Node(Object content) { next= null; this.content = content; }
    public Node(Object content, Node next) { this.next = next; this.content= content; }
    public void setNext(Node next) { this.next = next; }
    public void setContent(Object content) { this.content = content; }
    public Node getNext() { return next; }
    public Object getContent() { return content; }
  }
  
  private Node getPrevious() {
    if (current != null && current != first) {
      Node prev = first;
      while (prev.getNext() != current) { prev = prev.getNext(); }
      return prev;
    } else {
      return null;
    }
  }
  
  public List() { first = null; last= null; current = null; }
  public boolean isEmpty() { return first == null; }
  public boolean hasAccess() { return current != null; }
  public void next() { if (hasAccess()) current = current.getNext(); }
  public void toFirst() { current = first; }
  public void toLast() { current = last; }
  public Object getObject() { if (hasAccess()) return current.getContent(); else return null; }
  public void setObject(Object pObject) { if (hasAccess() && pObject != null) current.setContent(pObject); }

  public void append(Object pObject) {
    if (pObject != null) {
      if (isEmpty()) {
        Node newNode = new Node(pObject);
        first = newNode;
        last = newNode;
      } else {
        Node newNode = new Node(pObject);
        last.setNext(newNode);
        last = newNode;
      }
    }
  }
  
  public void insert(Object pObject) {
    if (pObject != null) {
      if (isEmpty()) {
        Node newNode = new Node(pObject);
        first = newNode;
        last = newNode;
      } else {
        if (hasAccess()) {
          if (current == first) { // Anfang der Liste einf�gen
            first = new Node(pObject, current);
          } else {                // mittendrineinf�gen
            Node newNode = new Node(pObject, current);
            Node prev = getPrevious();
            prev.setNext(newNode);
          }
        }
      }
    }
  }
  
  public void concat(List pList) {
    if (pList != null && !pList.isEmpty()) {
      pList.toFirst();
      while (!pList.isEmpty()) {
        Object pObject = pList.getObject();
        append(pObject);
        pList.remove();
      }
    }
  }
  
  public void remove() {
    if (hasAccess()) {
      if (current == first) {
        first = first.getNext();
        current = first;
      } else {
        if (current == last) {
          last = getPrevious();
          last.setNext(null);
          current = null;
        } else {
          Node prev = getPrevious();
          prev.setNext(current.getNext());
          current = current.getNext();
        }
      }
    }
  }

  // Ende Methoden
}
