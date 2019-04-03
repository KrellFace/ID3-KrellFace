// ECS629/759 Assignment 2 - ID3 Skeleton Code
// Author: Simon Dixon

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Arrays;
import java.util.List; 
import java.util.ArrayList; 
import java.lang.Math.*;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.lang.Object;

class ID3 {

	/** Each node of the tree contains either the attribute number (for non-leaf
	 *  nodes) or class number (for leaf nodes) in <b>value</b>, and an array of
	 *  tree nodes in <b>children</b> containing each of the children of the
	 *  node (for non-leaf nodes).
	 *  The attribute number corresponds to the column number in the training
	 *  and test files. The children are ordered in the same order as the
	 *  Strings in strings[][]. E.g., if value == 3, then the array of
	 *  children correspond to the branches for attribute 3 (named data[0][3]):
	 *      children[0] is the branch for attribute 3 == strings[3][0]
	 *      children[1] is the branch for attribute 3 == strings[3][1]
	 *      children[2] is the branch for attribute 3 == strings[3][2]
	 *      etc.
	 *  The class number (leaf nodes) also corresponds to the order of classes
	 *  in strings[][]. For example, a leaf with value == 3 corresponds
	 *  to the class label strings[attributes-1][3].
	 **/
	class TreeNode {

		TreeNode[] children;
		int value;

		public TreeNode(TreeNode[] ch, int val) {
			value = val;
			children = ch;
		} // constructor

		public String toString() {
			return toString("");
		} // toString()
		
		String toString(String indent) {
			if (children != null) {
				String s = "";
				for (int i = 0; i < children.length; i++)
					s += indent + data[0][value] + "=" +
							strings[value][i] + "\n" +
							children[i].toString(indent + '\t');
				return s;
			} else
				return indent + "Class: " + strings[attributes-1][value] + "\n";
		} // toString(String)

	} // inner class TreeNode

	private int attributes; 	// Number of attributes (including the class)
	private int examples;		// Number of training examples
	private TreeNode decisionTree;	// Tree learnt in training, used for classifying
	private String[][] data;	// Training data indexed by example, attribute
	private String[][] strings; // Unique strings for each attribute
	private int[] stringCount;  // Number of unique strings for each attribute

	//Store ordered class options
	private String[] classOpts;

	//Store Classes as indexes to output
	private String[] outputClasses;

	public ID3() {
		attributes = 0;
		examples = 0;
		decisionTree = null;
		data = null;
		strings = null;
		stringCount = null;
	} // constructor
	
	public void printTree() {
		if (decisionTree == null)
			error("Attempted to print null Tree");
		else
			System.out.println(decisionTree);
	} // printTree()

	/** Print error message and exit. **/
	static void error(String msg) {
		System.err.println("Error: " + msg);
		System.exit(1);
	} // error()

	static final double LOG2 = Math.log(2.0);
	
	static double xlogx(double x) {
		return x == 0? 0: x * Math.log(x) / LOG2;
	} // xlogx()

	/** Execute the decision tree on the given examples in testData, and print
	 *  the resulting class names, one to a line, for each example in testData.
	 **/
	public void classify(String[][] testData) {
		if (decisionTree == null)
			error("Please run training phase before classification");
		// PUT  YOUR CODE HERE FOR CLASSIFICATION

		outputClasses = new String[testData.length];

        ArrayList<Integer> initialTargetRows = new ArrayList<Integer>();
        for (int i = 1; i < testData.length; i++){
        	
        	initialTargetRows.add(i);

        }

		this.processNode(decisionTree, testData, initialTargetRows);
		//System.out.println("Created output classes array: " + Arrays.toString(outputClasses));
		//Print all output classes
		for (int i = 1; i < outputClasses.length; i++){
			
			System.out.println(outputClasses[i]);

		}
	} // classify()

	public void processNode(TreeNode inputNode, String[][] inputData, ArrayList targetRows){
		
		//System.out.println("Process node started");


		//Check if we have arrived at a leaf
		if (inputNode.children == null){
			
			//System.out.println("Child node found during clasification");

			for (int i = 0; i < targetRows.size(); i++){
				
				outputClasses[(Integer) targetRows.get(i)] = classOpts[inputNode.value];

			}


		}
		else {

			//System.out.println("Current num of children: " + inputNode.children.length);
			
			//Get attribute values from dataset

			String[] attributeVals = new String[data.length-1];

			for (int i = 1; i < data.length; i++){
				
				attributeVals[i-1] = data[i][inputNode.value];

			}

			//Debug
			//System.out.println("Attribute options to sub classify on: "  + Arrays.toString(attributeVals));

			String[] attribOpts = new LinkedHashSet<String>(Arrays.asList(attributeVals)).toArray(new String[0]);

			//Debug
			//System.out.println("Attribute options to sub classify on: "  + Arrays.toString(attribOpts));

			for (int i = 0; i < attribOpts.length; i++){
				
				ArrayList<Integer> newTargetRows = new ArrayList<Integer>();



				for (int j = 0; j < targetRows.size(); j++){
					
					//System.out.println("Inputnode value: " + inputNode.value + " and target row is " + (Integer) targetRows.get(j));
					//System.out.println("Checking if " + inputData[(Integer) targetRows.get(j)][inputNode.value] + " is equal to: " + attribOpts[i] );

					if (inputData[(Integer) targetRows.get(j)][inputNode.value].equals(attribOpts[i])){
						
						newTargetRows.add((Integer) targetRows.get(j));

					}

				}
				//System.out.println("New target rows for atttribute " + attribOpts[i] + " found: "  + Arrays.toString(newTargetRows.toArray()));


				this.processNode(inputNode.children[i], inputData, newTargetRows);

			}

			//System.out.println("Classify attributes found: " + Arrays.toString(attribOpts));


		}
	}


	public void train(String[][] trainingData) {
		indexStrings(trainingData);

		//Store original dataset
		data = trainingData;
		//Debug
		//System.out.println(Arrays.deepToString(trainingData));
		//First, we have to get the Attribute headings
		
		String[] headings = new String[trainingData[0].length - 1];
		for (int i  = 0; i < trainingData[0].length - 1 ; i++){
			
				//Store attribute headings
				headings[i] = trainingData[0][i];

		}

		String[] classValsOrig = new String[trainingData.length - 1];
		for (int i = 1; i < trainingData.length ; i++ ){

				classValsOrig[i - 1] = trainingData[i][trainingData[0].length-1];

		} 

		//Store class value options
		classOpts = new LinkedHashSet<String>(Arrays.asList(classValsOrig)).toArray(new String[0]);

		//Store original dataset for index retrieval
		String[][] origDataSet = trainingData;


		//Debug
		//System.out.println("Headings found: " + Arrays.toString(headings));

		decisionTree = this.calcBestSplit(trainingData, classOpts, headings, origDataSet);
		

	} // train()

	//Return best header to split on from dataset
	public TreeNode calcBestSplit(String[][] inputSet, String[] classOptsInput, String[] headingsInput, String[][] origDataSet){

		//System.out.println("Calbestsplit started");

		TreeNode localTreeNode;

		//Store the global class options for classification (this is a hack)
		String[] classOpts = classOptsInput;
		//Store the global headings for treenode creation
		String[] headings = headingsInput;

		//System.out.println("Class input Options found: "  + Arrays.toString(classOpts));

		//Store our return value of best option to split on
		String bestSplit = new String();
		double bestEntropy = -1.0D;

		//Retrive array of classes, and count of first class
		String[] classVals = new String[inputSet.length - 1];
		double class0Count = 0;
		for (int i = 1; i < inputSet.length ; i++ ){

				classVals[i - 1] = inputSet[i][inputSet[0].length-1];
				
				//System.out.println("Checking if this attribute in final column is equal to yes : " + classVals[i - 1]);
				if (classVals[i - 1].equals(classOpts[0])){
					class0Count+=1;

					//Debug
					//System.out.println("Class1 found in dataset final Q");
				}
		}

		//System.out.println("SucessFlags found for answer options: " + Arrays.deepToString(classVals));

		//Check to see if we have found a leaf node
		if (class0Count == inputSet.length - 1){
			
			//System.out.println("Leaf node found. Value: "  + classOpts[0]);

			localTreeNode = new TreeNode(null, 0);

		}


		else if (class0Count == 0){
			
			//System.out.println("Leaf node found. Value: " + classOpts[1]);

			localTreeNode = new TreeNode(null, 1);



		}
		//Terminate if no more splits available
		else if (inputSet[0].length < 2){
			
			//System.out.println("Out of attributes to check");

			if (class0Count > (inputSet.length/2)){
				
				localTreeNode = new TreeNode(null, 0);

			}
			else{
				
				localTreeNode = new TreeNode(null, 1);

			}

		}

		else {
			
			//Calculate total entropy

			double dataNum = classVals.length;

			double totalH = (-((class0Count/dataNum)*(this.log2(class0Count/dataNum)))-(((dataNum-class0Count)/dataNum)*(this.log2((dataNum-class0Count)/dataNum))));

			//Debug

			//System.out.println("Total entropy of set: " + totalH);
		

			//Calculate the entropy change for spliting on each header
			for (int i  = 0; i < inputSet[0].length - 1 ; i++){
				//Get unique answer values
				String[] answers = new String[inputSet.length - 1];
				for (int j = 1; j < inputSet.length ; j++ ){
					
					answers[j - 1] = inputSet[j][i];

				} 
				String[] ansOptions = new LinkedHashSet<String>(Arrays.asList(answers)).toArray(new String[0]);
				//Debug
				//System.out.println("Answers found for attribute " + headings[i].toString()+ " : " + Arrays.toString(answers) );
				//Debug
				//System.out.println("Answer Options found for attribute " + inputSet[0][i].toString()+ " : " + Arrays.toString(ansOptions ));


				//Instantiate storage for questions and scores. Stored as answer option index, successes i.e if this value gives a success state + 1, failures i.e if this value gives failure + 1
				int[][] ansScores = new int[ansOptions.length][3];
				//Loop through every answer option
				for (int ansOpt = 0; ansOpt < ansOptions.length; ansOpt++){
					ansScores[ansOpt][0] = ansOpt;
					ansScores[ansOpt][1] = 0;
					ansScores[ansOpt][2] = 0;
					for (int j = 0; j < answers.length ; j++ ){
					
						//If we find the current attribute value were checking for, check its success/fail flag in the final column
						//Debug
						//System.out.println("Checking if answer option " + ansOptions[ansOpt] + " is equal to answer: " + answers[j]);
						if (ansOptions[ansOpt].equals(answers[j])){

							//Debug
							//System.out.println("Match found");
							//Checking value of answer column
							//Debug
							//System.out.println("Checking if success flag option " + classVals[j] + " is set to Yes");
							if ( classVals[j].equals(classOpts[0])){
								ansScores[ansOpt][1]+=1;
							}
							else {
								ansScores[ansOpt][2]+=1;
							}
							
						}

					} 
				}
				//Debug
				//System.out.println("Scores found for answer options: " + Arrays.deepToString(ansScores));

				//Calculate information gain of choice
				double optionEntropy = totalH;

				for (int ansOpt = 0; ansOpt < ansOptions.length; ansOpt++){
					
					//Initialise attributes for entropy calculations
					double totalYesNo = ansScores[ansOpt][1] + ansScores[ansOpt][2];
					double yesNum = ansScores[ansOpt][1];
					double noNum = ansScores[ansOpt][2];

					//Debug
					//System.out.println("Total yes and nos for option: " + total);

					//Debug
					//System.out.println("Total yeses: " + ansScores[ansOpt][1]);

					//Debug 
					//System.out.println("Fraction to be log2'd: " + yesNum/total);

					double unpropedEnt;
					
					if ((yesNum == totalYesNo)||(noNum == totalYesNo)){
						
						unpropedEnt = 0;

					} 
					else { 

						unpropedEnt = (-((yesNum/totalYesNo) * this.log2(yesNum/totalYesNo)) -((noNum/totalYesNo) * this.log2(noNum/totalYesNo)));

					}
					
					//Debug
					//System.out.println("Unfractionated entropy of answer opt: " + ansOpt + " is " + unpropedEnt);

					//Update the overall header entropy gain for this answerOption
					optionEntropy -= unpropedEnt * ( totalYesNo/dataNum);

				}


				//Debug+
				//System.out.println("Entroy gain of choosing option:  " + optionEntropy);

				if (optionEntropy > bestEntropy){
					
					//System.out.println("New best split found");
					bestEntropy = optionEntropy;
					bestSplit = inputSet[0][i];

				}

		
			}

			//Debug
			//System.out.println("Best header split for set found: " + bestSplit); 	



			//Find the index of the header to split on within the data set

			int splitIndex = 0;

			for (int i = 0; i < inputSet[0].length; i ++){
			 	
				if (inputSet[0][i].equals(bestSplit)){
			 		
			 			splitIndex = i;

			 	}
			}
			//Capture the index of header to split on within the original header list
			int headingsSplitIndex = -1 ;

			for (int i = 0; i < headings.length; i++){
				
				if (headings[i].equals(bestSplit)){
					
					headingsSplitIndex = i;


				}

			}

			//System.out.println("Best split found: " + bestSplit + ". Local best split index: " + splitIndex + ". GLobal best split index: " + headingsSplitIndex);
	
			String[] ansInSplit = new String[inputSet.length - 1];
			for (int i = 1; i < inputSet.length ; i++ ){
					
				ansInSplit[i - 1] = inputSet[i][splitIndex];

			} 

			//Capture original answer options for capturing uniques in the right order
			String[] origAnsList = new String[origDataSet.length - 1];
			for (int i = 1; i < origDataSet.length ; i++ ){
					
				origAnsList[i - 1] = origDataSet[i][headingsSplitIndex];

			} 	

			//Capturing unique answer options in split
			String[] ansInSplitOpts = new LinkedHashSet<String>(Arrays.asList(origAnsList)).toArray(new String[0]);



			//Set value of current node
			localTreeNode = new TreeNode(new TreeNode[ansInSplitOpts.length], headingsSplitIndex);



			//System.out.println("AnsOptions found in dataset split function: " + Arrays.toString(ansInSplitOpts));
			
			for (int i = 0; i < ansInSplitOpts.length; i++){

				int ansOptCount = 0;
				
				for (int j = 0; j < ansInSplit.length; j++){
					
					if (ansInSplitOpts[i].equals(ansInSplit[j])){
						
						ansOptCount +=1;

					}

				}

				//Debug
				//System.out.println("Answer option count (to create sub array size): " + ansOptCount);

				//Initiate sub array which will be ID3'd
				String [][] subArrayToTest = new String[ansOptCount+1][inputSet[0].length - 1];

				//Insert headers
				for (int j = 0; j < subArrayToTest[0].length; j++){
					
					if (j<splitIndex){
						
						subArrayToTest[0][j]=inputSet[0][j];

					}
					else {
						
						subArrayToTest[0][j]=inputSet[0][j+1];

					}

				}

				//Debug
				//System.out.println(Arrays.deepToString(subArrayToTest));

				int rowToInsert = 1;

				//Insert all data into new subgroup
				for (int j = 1; j < inputSet.length; j++){

					


					//Check if row is one with matching answer
					if (inputSet[j][splitIndex].equals(ansInSplitOpts[i])){

						//Debug
						//System.out.println("Found matching row. Row to insert : " + rowToInsert);
					
						for (int k = 0; k < subArrayToTest[0].length; k++){
							
							

							if (k<splitIndex){
							
								subArrayToTest[rowToInsert][k]=inputSet[j][k];

							}

							else{

								subArrayToTest[rowToInsert][k] =inputSet[j][k + 1];

							}
							

						}

						rowToInsert += 1;

					}

					//Debug
					//System.out.println(Arrays.deepToString(subArrayToTest));

				}

				//Debug
				//System.out.println(Arrays.deepToString(subArrayToTest));

				//System.out.println("Searching new subgroup, split based on header : " + headings[headingsSplitIndex] + " and value: " + ansInSplitOpts[i] );

				localTreeNode.children[i] = this.calcBestSplit(subArrayToTest, classOpts, headings, origDataSet);

			}

			/*
			String[] answers = new String[dataSet.length - 1];
			for (int i = 1; i < dataSet.length ; i++ ){
					
				answers[i - 1] = dataSet[i][splitIndex];

			} 
			String[] ansOptions = Arrays.stream(answers).distinct().toArray(String[]::new);

			System.out.println("AnsOptions found in dataset split function: " + Arrays.toString(ansOptions));
			*/

			}


		//System.out.println("Local tree node before returning it: " + localTreeNode.toString());

		//System.out.println("Calcbestsplit ended");
		
		return localTreeNode;
	}

	public String[][][] splitData(String[][][] dataSet, String splitOn){
		
		return dataSet;

	}

	//Calculate log2
	public double log2(double input){
		
		return (Math.log(input)/Math.log(2));

	}


	/** Given a 2-dimensional array containing the training data, numbers each
	 *  unique value that each attribute has, and stores these Strings in
	 *  instance variables; for example, for attribute 2, its first value
	 *  would be stored in strings[2][0], its second value in strings[2][1],
	 *  and so on; and the number of different values in stringCount[2].
	 **/
	void indexStrings(String[][] inputData) {
		data = inputData;
		examples = data.length;
		attributes = data[0].length;
		stringCount = new int[attributes];
		strings = new String[attributes][examples];// might not need all columns
		int index = 0;
		for (int attr = 0; attr < attributes; attr++) {
			stringCount[attr] = 0;
			for (int ex = 1; ex < examples; ex++) {
				for (index = 0; index < stringCount[attr]; index++)
					if (data[ex][attr].equals(strings[attr][index]))
						break;	// we've seen this String before
				if (index == stringCount[attr])		// if new String found
					strings[attr][stringCount[attr]++] = data[ex][attr];
			} // for each example
		} // for each attribute
	} // indexStrings()

	/** For debugging: prints the list of attribute values for each attribute
	 *  and their index values.
	 **/
	void printStrings() {
		for (int attr = 0; attr < attributes; attr++)
			for (int index = 0; index < stringCount[attr]; index++)
				System.out.println(data[0][attr] + " value " + index +
									" = " + strings[attr][index]);
	} // printStrings()
		
	/** Reads a text file containing a fixed number of comma-separated values
	 *  on each line, and returns a two dimensional array of these values,
	 *  indexed by line number and position in line.
	 **/
	static String[][] parseCSV(String fileName)
								throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String s = br.readLine();
		int fields = 1;
		int index = 0;
		while ((index = s.indexOf(',', index) + 1) > 0)
			fields++;
		int lines = 1;
		while (br.readLine() != null)
			lines++;
		br.close();
		String[][] data = new String[lines][fields];
		Scanner sc = new Scanner(new File(fileName));
		sc.useDelimiter("[,\n]");
		for (int l = 0; l < lines; l++)
			for (int f = 0; f < fields; f++)
				if (sc.hasNext())
					data[l][f] = sc.next();
				else
					error("Scan error in " + fileName + " at " + l + ":" + f);
		sc.close();
		return data;
	} // parseCSV()

	public static void main(String[] args) throws FileNotFoundException,
												  IOException {
		if (args.length != 2)
			error("Expected 2 arguments: file names of training and test data");
		String[][] trainingData = parseCSV(args[0]);
		String[][] testData = parseCSV(args[1]);
		ID3 classifier = new ID3();
		classifier.train(trainingData);
		classifier.printTree();
		classifier.classify(testData);
	} // main()

} // class ID3
