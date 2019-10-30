package ir.vsr;

import ir.utilities.UserInput;

public class FeedbackRated extends Feedback {
    public  final String USER_PROMPT_ERROR_MSG = "Please enter an integer or floating point number between -1 and 1";
    public FeedbackRated(HashMapVector queryVector, Retrieval[] retrievals, InvertedIndex invertedIndex) {
        super(queryVector, retrievals, invertedIndex);
    }

    public void addGood(DocumentReference docRef, double rating) {
        docRef.feedBackScore = rating;
        goodDocRefs.add(docRef);
    }

    public void addBad(DocumentReference docRef, double rating) {
        docRef.feedBackScore = rating;
        badDocRefs.add(docRef);
    }

    public void addFeedback(int showNumber, double rating) {
        // Get the docRef for this document (remember showNumber starts at 1 and is 1 greater than array index)
        DocumentReference docRef = retrievals[showNumber - 1].docRef;
        if (rating < 0)
            addBad(docRef, -rating);
        else
            addGood(docRef, rating);
    }

    @Override
    public void getFeedback(int showNumber) {
        // Get the docRef for this document (remember showNumber starts at 1 and is 1 greater than array index)
        DocumentReference docRef = retrievals[showNumber - 1].docRef;
        String response = UserInput.prompt("Is document #" + showNumber + ":" + docRef.file.getName() +
                " relevant (enter a number between -1 and 1 where -1:" +
                " very irrelevant, 0: unsure, +1: very relevant)?: ");
        double responseValue = 0.0;
        try {
            responseValue = Double.parseDouble(response);
            if (responseValue < -1 || responseValue > 1) {
                System.out.println(USER_PROMPT_ERROR_MSG);
                getFeedback(showNumber);
            }
            else {
                if (responseValue < 0)
                    addBad(docRef, -responseValue);
                else
                    addGood(docRef, responseValue);
            }
        }
        catch (NumberFormatException e) {
            System.out.println(USER_PROMPT_ERROR_MSG);
            getFeedback(showNumber);
        }
    }

    /**
     * Use the Ide_regular algorithm to compute a new revised query.
     *
     * @return The revised query vector.
     */
    @Override
    public HashMapVector newQuery() {
        // Start the query as a copy of the original
        HashMapVector newQuery = queryVector.copy();
        // Normalize query by maximum token frequency and multiply by alpha
        newQuery.multiply(ALPHA / newQuery.maxWeight());
        // Add in the vector for each of the positively rated documents
        for (DocumentReference docRef : goodDocRefs) {
            // Get the document vector for this positive document
            Document doc = docRef.getDocument(invertedIndex.docType, invertedIndex.stem);
            HashMapVector vector = doc.hashMapVector();
            // Multiply positive docs by beta and normalize by max token frequency
            vector.multiply((BETA * docRef.feedBackScore) / vector.maxWeight());
            // Add it to the new query vector
            newQuery.add(vector);
        }
        // Subtract the vector for each of the negatively rated documents
        for (DocumentReference docRef : badDocRefs) {
            // Get the document vector for this negative document
            Document doc = docRef.getDocument(invertedIndex.docType, invertedIndex.stem);
            HashMapVector vector = doc.hashMapVector();
            System.out.println(docRef + ", rating: " + docRef.feedBackScore);
            // Multiply negative docs by beta and normalize by max token frequency
            vector.multiply((GAMMA * docRef.feedBackScore) / vector.maxWeight());
            // Subtract it from the new query vector
            newQuery.subtract(vector);
        }
        return newQuery;
    }
}
