
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.dom.*;

public class AstVisitor extends ASTVisitor {
	Stack<String> klass = new Stack<String>();
	String pakage;
	
	public String getKlass() {
		return klass.get(0);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		klass.push(node.getName().getFullyQualifiedName());
		return super.visit(node);
	}

	@Override
	public void endVisit(TypeDeclaration node) {
		if (klass.size() > 1)
			klass.pop();
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodRef node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		pakage = node.getName().getFullyQualifiedName();
		return super.visit(node);
	}

	public boolean DEBUG;
	
	List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();

	@Override
	public boolean visit(MethodInvocation node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		methods.add(node);
		return super.visit(node);
	}

	public List<MethodDeclaration> getMethods() {
		return methods;
	}
}
