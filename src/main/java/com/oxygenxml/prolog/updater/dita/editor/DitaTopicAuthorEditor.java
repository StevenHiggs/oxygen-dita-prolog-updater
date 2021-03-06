package com.oxygenxml.prolog.updater.dita.editor;

import java.util.List;

import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import com.oxygenxml.prolog.updater.prolog.content.PrologContentCreator;
import com.oxygenxml.prolog.updater.utils.AuthorPageDocumentUtil;
import com.oxygenxml.prolog.updater.utils.ElementXPathUtils;
import com.oxygenxml.prolog.updater.utils.XMLFragmentUtils;
import com.oxygenxml.prolog.updater.utils.XmlElementsConstants;
import com.oxygenxml.prolog.updater.utils.XmlElementsUtils;

import ro.sync.ecss.extensions.api.AuthorConstants;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.node.AttrValue;
import ro.sync.ecss.extensions.api.node.AuthorElement;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.ditamap.WSDITAMapEditorPage;

/**
 * Edit DITA topic in author mode.
 * 
 * @author cosmin_duna
 */
public class DitaTopicAuthorEditor implements DitaEditor{

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(DitaTopicAuthorEditor.class);
	 
	/**
	 * Contains all elements(tags) from prolog.
	 */
	private PrologContentCreator prologCreator;
	
	/**
	 * Author document controller
	 */
	private AuthorDocumentController documentController;

	/**
	 *The document type( {@link DocumentType#TOPIC}, {@link DocumentType#MAP} or {@link DocumentType#BOOKMAP}  ).
	 */
	private DocumentType documentType = DocumentType.TOPIC;

	/**
	 * The page from WsEditor.
	 */
  private WSEditorPage page;
	
	/**
	 * Constructor
	 * @param page The page from WSEditor.
	 * @param prologContentCreater Contains all elements from prolog.
	 */
	public DitaTopicAuthorEditor(WSEditorPage page, PrologContentCreator prologContentCreator) {
	  this.page = page;
	  
    if(page instanceof WSAuthorEditorPage) {
	    this.documentController = ((WSAuthorEditorPage) page).getDocumentController();
	  } else if(page instanceof WSDITAMapEditorPage) {
	    this.documentController = ((WSDITAMapEditorPage)page).getDocumentController();
	  }
		
    if(documentController != null) {
    	AuthorElement rootElement = documentController.getAuthorDocumentNode().getRootElement();
    	AttrValue classValue = rootElement.getAttribute(XmlElementsConstants.CLASS);
    	if (classValue != null && classValue.getValue().contains(" map/map ")) {
    		documentType = DocumentType.MAP;
    	}
    	if (classValue != null && classValue.getValue().contains(" bookmap/bookmap ")) {
    		documentType = DocumentType.BOOKMAP;
    	}
    	
    	prologCreator = prologContentCreator;
    }
	}
	
	
	/**
	 * Update the prolog in DITA topic document(author mode) according to given flag(isNewDocument)
	 * @param isNewDocument <code>true</code> if document is new, <code>false</code> otherwise
	 * 
	 * @return <code>true</code> if prolog was update, <code>false</code> otherwise.
	 */
	public boolean updateProlog(boolean isNewDocument) {
		boolean toReturn = true;
		if(documentController != null) {
			// Get the root element.
			AuthorElement rootElement = documentController.getAuthorDocumentNode().getRootElement();
			
			if (rootElement != null) {
				// Get the prolog element.
				AuthorElement prolog = AuthorPageDocumentUtil.findElementByClass(rootElement, XmlElementsUtils.getPrologClass(documentType));
				try {
					if (prolog != null) {
						// Prolog element exists; edit this element.
						editProlog(prolog, isNewDocument);
					} else {
						// Add the prolog element.
						addProlog(isNewDocument);
					}
				} catch (AuthorOperationException e) {
					toReturn = false;
				}
			}
		} else {
			toReturn = false;
		}
		
		return toReturn;
	}

	
	 /**
   * Add the prolog element.
   * @param isNewDocument <code>true</code> if document is new, <code>false</code>otherwise.
	 * @throws AuthorOperationException If the prolog could not be added.
   */
	private void addProlog(boolean isNewDocument) throws AuthorOperationException {
		String prologFragment = prologCreator.getPrologFragment(isNewDocument, documentType);
		String prologXpath = AuthorPageDocumentUtil.findPrologXPath(documentController, documentType);
		
		if(prologXpath != null) {
				AuthorPageDocumentUtil.insertFragmentSchemaAware(page, documentController, prologFragment, prologXpath, AuthorConstants.POSITION_AFTER);
		}	else {
			AuthorPageDocumentUtil.insertFragmentSchemaAware(page, documentController, prologFragment, ElementXPathUtils.getRootXpath(documentType),
					AuthorConstants.POSITION_INSIDE_FIRST);
		}
	}

	
	/**
	 * Edit the given prolog element.
	 * @param critdates The element to be edited. <code>Not null</code>
	 * @param isNewDocument  <code>true</code> if document is new, <code>false</code>otherwise.
	 * @throws AuthorOperationException If the prolog could not be edited.
	 */
	private void editProlog(AuthorElement prolog, boolean isNewDocument) throws AuthorOperationException {
			// Updates the creators and/or contributors of document
			updateAuthorElements(prolog, isNewDocument);
			
			// Update the critdates element .
			updateCritdates(prolog, isNewDocument);
	}

	
  /**
   * Update the critdates element.
   * 
   * @param prolog The prolog author element. Not <code>null</code>.
   * @param isNewDocument <code>true</code> if document is new, <code>false</code>otherwise.
   * @throws AuthorOperationException If the element could not be update.
   */
  private void updateCritdates(AuthorElement prolog, boolean isNewDocument) throws AuthorOperationException {
    // Where to insert
    AuthorElement cridates = AuthorPageDocumentUtil.findElementByClass(prolog, XmlElementsConstants.TOPIC_CRITDATES_CLASS);
    if (cridates != null) {
    	// The critdates element exists, edit the content of this element.
    	editCritdates(cridates, isNewDocument);
    } else {
    	// The critdates element doesn't exist, add this element.
    	addCritdates(prolog, isNewDocument);
    }
  }

  
  /**
   * Add the critdates element into the given prolog element.
   * @param prolog The prolog element where the critdates is add. <code>Not null</code>  
   * @param isNewDocument <code>true</code> if document is new, <code>false</code>otherwise.
   * @throws AuthorOperationException If the element could not be added.
   */
	private void addCritdates(AuthorElement prolog, boolean isNewDocument) throws AuthorOperationException {
		int offset = -1;
		String fragment = null;
		List<AuthorElement> authors = AuthorPageDocumentUtil.findElementsByClass(prolog, XmlElementsConstants.PROLOG_AUTHOR_ELEMENT_CLASS);
		// Create an element here.
		fragment = XMLFragmentUtils.createCritdateTag(prologCreator.getDateFragment(isNewDocument, documentType));
		if(authors.isEmpty()) {
		  offset = prolog.getEndOffset();
		} else {
		  AuthorElement lastAuthorElement = authors.get(authors.size()-1);
		  offset = lastAuthorElement.getEndOffset() + 1;
		}
		AuthorPageDocumentUtil.insertFragmentSchemaAware(page, documentController, fragment, offset);
	}
  
	
	/**
	 * Edit the given critdate element.
	 * @param critdates The element to be edited. <code>Not null</code>
	 * @param isNewDocument  <code>true</code> if document is new, <code>false</code>otherwise.
	 * @throws AuthorOperationException If the element could not be edited.
	 */
  private void editCritdates(AuthorElement critdates, boolean isNewDocument) throws AuthorOperationException {
    if (isNewDocument) {
      AuthorElement createdElement = AuthorPageDocumentUtil.findElementByClass(critdates, XmlElementsConstants.CREATED_DATE_ELEMENT_CLASS);
      // Was not added yet. 
      if (createdElement == null) {
        // Add it.
      	AuthorPageDocumentUtil.insertFragmentSchemaAware(page, 
      			documentController,
      			prologCreator.getCreatedDateFragment(documentType),
      			critdates.getStartOffset() + 1);
      }
    } else {
      // it's not a new document
      // add revised element
			addRevisedElement(critdates);
    }
  
  }
  
  
	/**
	 * Add the revised element if it doesn't exits.
	 * @param critdatesElement critdates element(element that has revised child). <code>Not null</code>
	 * @throws AuthorOperationException If the element could not be added.
	 */
	private void addRevisedElement(AuthorElement critdatesElement) throws AuthorOperationException{
		boolean localDateWithAuthorCommentExist = false;

		// get revised elements
		List<AuthorElement> revisedElements = AuthorPageDocumentUtil.findElementsByClass(critdatesElement, XmlElementsConstants.REVISED_DATE_ELEMENT_CLASS);
		int revisedElementSize = revisedElements.size();

		// Iterate over revised elements
		for (AuthorElement current : revisedElements) {
		  // check the modified value.
		  AttrValue modifiedDate = current.getAttribute(XmlElementsConstants.MODIFIED_ATTRIBUTE);
		  if (modifiedDate != null && prologCreator.getLocalDate().equals(modifiedDate.getRawValue())) {
				try {
					// Get the previous node
					AuthorNode previousSibling = documentController.getNodeAtOffset(current.getStartOffset() - 1);
					// and check if it's a comment.
					if (previousSibling.getType() == AuthorNode.NODE_TYPE_COMMENT
							&& prologCreator.getAuthor().equals(previousSibling.getTextContent())) {
						localDateWithAuthorCommentExist = true;
						break;
					}
				} catch (BadLocationException e) {
					logger.debug(e.getMessage(), e);
				}
		  }
		}
		if (!localDateWithAuthorCommentExist) {
		  int offset = critdatesElement.getEndOffset();
		  String fragment = prologCreator.getRevisedDateFragment(documentType);
			if (revisedElementSize != 0) {
        offset = revisedElements.get(revisedElementSize -1).getEndOffset()+1;
			}
			// Now insert it.
			AuthorPageDocumentUtil.insertFragmentSchemaAware(page, documentController, fragment, offset);
		}
	}

	
	/**
   * Update the document adding the names of the authors.
   * 
   * @param prolog The prolog element. Never <code>null</code>.
   * @param isNewDocument <code>true</code> if document is new, <code>false</code>otherwise.
   * 
	 * @throws AuthorOperationException If the element could not be update.
   */
  private void updateAuthorElements(AuthorElement prolog, boolean isNewDocument) throws AuthorOperationException {
		String type = isNewDocument ? XmlElementsConstants.CREATOR_TYPE : XmlElementsConstants.CONTRIBUTOR_TYPE;
  	
  	List<AuthorElement> authors = AuthorPageDocumentUtil.findElementsByClass(prolog, XmlElementsConstants.PROLOG_AUTHOR_ELEMENT_CLASS);
    final int length = authors.size();
    
    // Search for author with given type.
    boolean hasAuthor = AuthorPageDocumentUtil.hasAuthor(authors, type, prologCreator.getAuthor());
    
    String fragment = null;
    int offset = prolog.getStartOffset() + 1;
    
    if (!hasAuthor && XmlElementsConstants.CONTRIBUTOR_TYPE.equals(type)) {
      // if wasn't found this contributor
      fragment = prologCreator.getContributorFragment(documentType);
      if(length > 0){
        AuthorElement lastAuthor = authors.get(length - 1);
        offset = lastAuthor.getEndOffset() + 1;
      }
    } else if (!hasAuthor && XmlElementsConstants.CREATOR_TYPE.equals(type)) {
      fragment = prologCreator.getCreatorFragment(documentType);
    }
    
    AuthorPageDocumentUtil.insertFragmentSchemaAware(page, documentController, fragment, offset);
  }
}
