/**
 * 
 */
package org.wltea.expression.incoding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.wltea.expression.ExpressionExecutor;
import org.wltea.expression.ExpressionToken;
import org.wltea.expression.ExpressionTokenHelper;
import org.wltea.expression.IllegalExpressionException;
import org.wltea.expression.datameta.BaseDataMeta;
import org.wltea.expression.datameta.Constant;
import org.wltea.expression.datameta.Variable;
import org.wltea.expression.datameta.VariableContainer;
import org.wltea.expression.function.FunctionExecution;
import org.wltea.expression.op.Operator;

/**
 * 编码中的新执行器
 * @author Administrator
 * 
 */
public class ExpressionExecutorInCoding extends ExpressionExecutor {

	private int inShortCutStatus = 0;	
	
	public List<ExpressionToken> convertToRPN(List<ExpressionToken> expTokens)throws IllegalExpressionException{
		
		if(expTokens == null || expTokens.isEmpty()){
			throw new IllegalArgumentException("无法转化空的表达式");
		}
		
		//1.初始化逆波兰式队列和操作符栈
		List<ExpressionToken> _RPNExpList = new ArrayList<ExpressionToken>();
		Stack<ExpressionToken> opStack = new Stack<ExpressionToken>();
		//初始化检查栈
		Stack<ExpressionToken> verifyStack = new Stack<ExpressionToken>();
		
		//2.出队列中从左向右依次便利token
		//2-1. 声明一个存储函数词元的临时变量
		ExpressionToken _function = null;

		for(ExpressionToken expToken : expTokens){
			
			if(ExpressionToken.ETokenType.ETOKEN_TYPE_CONSTANT == expToken.getTokenType()){
				//读入一个常量，压入逆波兰式队列
				_RPNExpList.add(expToken);
				//同时压入校验栈
				verifyStack.push(expToken);
				
			} else if (ExpressionToken.ETokenType.ETOKEN_TYPE_VARIABLE == expToken.getTokenType()){
				//验证变量声明	
				Variable var = VariableContainer.getVariable(expToken.getVariable().getVariableName());
				if(var == null || var.getDataType() == null){
					throw new IllegalExpressionException("表达式不合法，变量\"" + expToken.toString() + "\"缺少定义;位置:" + expToken.getStartPosition()
								, expToken.toString()
								, expToken.getStartPosition());						
				}else{
					//设置Token中的变量类型定义
					expToken.getVariable().setDataType(var.getDataType());
				}
				
				//读入一个变量，压入逆波兰式队列
				_RPNExpList.add(expToken);
				//同时压入校验栈
				verifyStack.push(expToken);
				
				
			} else if  (ExpressionToken.ETokenType.ETOKEN_TYPE_OPERATOR == expToken.getTokenType()){		
				//读入一个操作符
				if(opStack.empty()){
					//如果操作栈为空，则压入栈内；
					opStack.push(expToken);
					
				}else{
					boolean doPeek = true;
					while(!opStack.empty() && doPeek){
						//如果栈不为空，则比较栈顶的操作符的优先级
						ExpressionToken onTopOp = opStack.peek();
						
						//如果栈顶元素是函数,直接将操作符压入栈
						if( ExpressionToken.ETokenType.ETOKEN_TYPE_FUNCTION == onTopOp.getTokenType() ){
							opStack.push(expToken);
							doPeek = false;
							
						}else if( ExpressionToken.ETokenType.ETOKEN_TYPE_SPLITOR == onTopOp.getTokenType()
									&& "(".equals(onTopOp.getSplitor())){
							opStack.push(expToken);
							doPeek = false;
							
						}else if(ExpressionToken.ETokenType.ETOKEN_TYPE_OPERATOR == onTopOp.getTokenType()){
							//如果栈顶元素是操作符							
							if(expToken.getOperator().getPiority() > onTopOp.getOperator().getPiority()){
								//当前操作符的优先级 > 栈顶操作符的优先级 ，则将当前操作符入站
								opStack.push(expToken);
								doPeek = false;
							}else{
								//当前操作符的优先级 <= 栈顶操作符的优先级，则将栈顶的操作符弹出
								//执行操作符校验
								ExpressionToken result = verifyOperator(onTopOp, verifyStack);
								//把校验结果压入检验栈
								verifyStack.push(result);
								
								//校验通过，，加入到逆波兰式队列
								opStack.pop();
								_RPNExpList.add(onTopOp);
								
							}
						}
					}
					//当前操作符的优先级 <= 栈内所有的操作符优先级
					if(doPeek && opStack.empty()){
						opStack.push(expToken);
					}
				}
				
				//进行短路优化
				//如果当前操作符为&&，要向逆波兰式压入{
				//如果当前操作符为||，要向逆波兰式压入}
				if("&&".equals(expToken.toString())){
					_RPNExpList.add(ExpressionToken.createSplitorToken("{") );
				}else if("||".equals(expToken.toString())){
					_RPNExpList.add(ExpressionToken.createSplitorToken("}") );
				}

				
			} else if (ExpressionToken.ETokenType.ETOKEN_TYPE_FUNCTION == expToken.getTokenType()){
				//遇到函数名称，则使用临时变量暂存下来，等待(的来临
				_function = expToken;

			} else if (ExpressionToken.ETokenType.ETOKEN_TYPE_SPLITOR == expToken.getTokenType()){
				//处理读入的“（”
				if("(".equals(expToken.getSplitor())){
					//如果此时_function != null,说明是函数的左括号
					if(_function != null){
						//向逆波兰式队列压入"("
						_RPNExpList.add(expToken);
						//向校验栈压入
						verifyStack.push(expToken);
						
						//将"("及临时缓存的函数压入操作符栈,括号在前
						opStack.push(expToken);
						opStack.push(_function);
						
						//清空临时变量
						_function = null;
						
					}else{
						//说明是普通的表达式左括号
						//将"("压入操作符栈
						opStack.push(expToken);						
					}
					
				//处理读入的“）”	
				}else if(")".equals(expToken.getSplitor())){
					
					boolean doPop = true;

					while(doPop && !opStack.empty()){						
						// 从操作符栈顶弹出操作符或者函数，
						ExpressionToken onTopOp = opStack.pop();
						
						if(ExpressionToken.ETokenType.ETOKEN_TYPE_OPERATOR == onTopOp.getTokenType()){
							//如果栈顶元素是普通操作符,执行操作符校验
							ExpressionToken result = verifyOperator(onTopOp, verifyStack);
							//把校验结果压入检验栈
							verifyStack.push(result);

							// 校验通过，则添加到逆波兰式对列
							_RPNExpList.add(onTopOp);
							
						}else if(ExpressionToken.ETokenType.ETOKEN_TYPE_FUNCTION == onTopOp.getTokenType()){
							// 如果遇到函数，则说明")"是函数的右括号
							//执行函数校验
							ExpressionToken result = verifyFunction(onTopOp , verifyStack);
							//把校验结果压入检验栈
							verifyStack.push(result);
							
							//校验通过，添加")"到逆波兰式中
							_RPNExpList.add(expToken);
							//将函数加入逆波兰式
							_RPNExpList.add(onTopOp);							
							
						}else if("(".equals(onTopOp.getSplitor())){
							// 如果遇到"(", 则操作结束
							doPop = false;
						}					
					}
					
					if(doPop && opStack.empty()){
						throw new IllegalExpressionException("在读入\")\"时，操作栈中找不到对应的\"(\" , 位置：0"
								, expToken.getSplitor()
								, expToken.getStartPosition());
					}
				
				//处理读入的“,”	
				}else if(",".equals(expToken.getSplitor())){
					//依次弹出操作符栈中的所有操作符，压入逆波兰式队列，直到遇见函数词元
					boolean doPeek = true;
					
					while(!opStack.empty() && doPeek){
						ExpressionToken onTopOp = opStack.peek();
						
						if(ExpressionToken.ETokenType.ETOKEN_TYPE_OPERATOR == onTopOp.getTokenType()){
							//弹出操作符栈顶的操作符
							opStack.pop();
							//执行操作符校验
							ExpressionToken result = verifyOperator(onTopOp, verifyStack);
							//把校验结果压入检验栈
							verifyStack.push(result);
							//校验通过，，压入逆波兰式队列
							_RPNExpList.add(onTopOp);
							
						}else if(ExpressionToken.ETokenType.ETOKEN_TYPE_FUNCTION == onTopOp.getTokenType()){
							//遇见函数词元,结束弹出
							doPeek = false;
							
						}else if(ExpressionToken.ETokenType.ETOKEN_TYPE_SPLITOR == onTopOp.getTokenType() 
									&& "(".equals(onTopOp.getSplitor())){
							//在读入","时，操作符栈顶为"(",则报错
							throw new IllegalExpressionException("在读入\",\"时，操作符栈顶为\"(\",,(函数丢失) 位置：" + onTopOp.getStartPosition()
									, expToken.getSplitor()
									, expToken.getStartPosition());
						}
					}
					//栈全部弹出，但没有遇见函数词元
					if(doPeek && opStack.empty()){
						throw new IllegalExpressionException("在读入\",\"时，操作符栈弹空，没有找到相应的函数词元 ,位置：0" 
								, expToken.getSplitor()
								, expToken.getStartPosition());
					}					
				}				
			}	
		}
		
		//将操作栈内剩余的操作符逐一弹出，并压入逆波兰式队列
		while(!opStack.empty()){
			ExpressionToken onTopOp = opStack.pop();
		
			if(ExpressionToken.ETokenType.ETOKEN_TYPE_OPERATOR == onTopOp.getTokenType() ){				
				//执行操作符校验
				ExpressionToken result = verifyOperator(onTopOp, verifyStack);
				//把校验结果压入检验栈
				verifyStack.push(result);
				
				//校验成功,将操作符加入逆波兰式				
				_RPNExpList.add(onTopOp);
			
			}else if(ExpressionToken.ETokenType.ETOKEN_TYPE_FUNCTION == onTopOp.getTokenType()){
				//如果剩余是函数，则函数缺少右括号")"
				throw new IllegalExpressionException("函数" + onTopOp.getFunctionName() + "缺少\")\"" 
						, onTopOp.getFunctionName()
						, onTopOp.getStartPosition());						
				
			}else if("(".equals(onTopOp.getSplitor())){
				//剩下的就只有“(”了，则说明表达式的算式缺少右括号")"
				throw new IllegalExpressionException("左括号\"(\"缺少配套的右括号\")\"" 
						, onTopOp.getFunctionName()
						, onTopOp.getStartPosition());						
			}			
		}
		
		//表达式校验完成，这是校验栈内应该只有一个结果,否则视为表达式不完成
		if(verifyStack.size() != 1){

			StringBuffer errorBuffer = new StringBuffer("\r\n");
			while(!verifyStack.empty()){
				ExpressionToken onTop = verifyStack.pop();
				errorBuffer.append("\t").append(onTop.toString()).append("\r\n");
			}
			throw new IllegalExpressionException("表达式不完整.\r\n 校验栈状态异常:" + errorBuffer);						

		}
		
		return _RPNExpList;
	}	
	
	public ExpressionToken executeRPN(List<ExpressionToken> _RPNExpList){
		if(_RPNExpList == null || _RPNExpList.isEmpty()){
			throw new IllegalArgumentException("无法执行空的逆波兰式队列");
		}
		
		//初始化执行栈
		Stack<ExpressionToken> executeStack = new Stack<ExpressionToken>();
		
		for(ExpressionToken expToken : _RPNExpList){
			
			//对||和&&运算进行优化处理
			if(inShortCutStatus > 0){
				//当短路表示为大于0时，遇到}号，继续+1，遇到||则-1 ，直到0
				if("||".equals(expToken.toString())){
					inShortCutStatus--;					
				}else if("}".equals(expToken.toString())){
					inShortCutStatus++;
				}
				if(inShortCutStatus > 0){
					continue;
				}
			}else if(inShortCutStatus < 0){
				//当短路表示为-1时，遇到{号，继续-1，遇到&&则+1 ，直到0
				if("&&".equals(expToken.toString())){
					inShortCutStatus++;					
				}else if("{".equals(expToken.toString())){
					inShortCutStatus--;
				}
				if(inShortCutStatus < 0){
					continue;
				}				
			}

			
			if(ExpressionToken.ETokenType.ETOKEN_TYPE_CONSTANT == expToken.getTokenType()){
				//读取一个常量，压入栈
				executeStack.push(expToken);
				
			}else if (ExpressionToken.ETokenType.ETOKEN_TYPE_VARIABLE == expToken.getTokenType()){
				//读取一个变量
				//从上下文获取变量的实际值，将其转化成常量Token，压入栈
				Variable varWithValue = VariableContainer.getVariable(expToken.getVariable().getVariableName());
				if(varWithValue != null){
					//生成一个有值常量，varWithValue.getDataValue有可能是空值
					ExpressionToken constantToken = ExpressionToken.createConstantToken(
										varWithValue.getDataType()
										, varWithValue.getDataValue());
					executeStack.push(constantToken);
					
				}else{
					throw new IllegalStateException("变量\"" +expToken.getVariable().getVariableName() + "\"不是上下文合法变量" );						
				}
				
				
			}else if (ExpressionToken.ETokenType.ETOKEN_TYPE_OPERATOR == expToken.getTokenType()){
				//栈为空
				if(executeStack.empty()){
					throw new IllegalStateException("操作符" + expToken.getOperator().getToken() + "找不到相应的参数，或参数个数不足;位置：" + expToken.getStartPosition());						
				}
				
				//对||和&&运算进行优化处理的短路处理
				if("&&".equals(expToken.toString())){
					//当&&号碰到栈顶的{,则弹出{,并忽略&&
					ExpressionToken onTop = executeStack.peek();
					if("{".equals(onTop.toString())){
						executeStack.pop();
						continue;
					}
				}else if ("||".equals(expToken.toString())){
					//当||号碰到栈顶的},则弹出},并忽略||
					ExpressionToken onTop = executeStack.peek();
					if("}".equals(onTop.toString())){
						executeStack.pop();
						continue;
					}					
				}
				
				//判定几元操作符
				int opType = expToken.getOperator().getOpType();
				//取得相应的参数个数
				Constant[] args = new Constant[opType];
				ExpressionToken argToken = null;
				for(int i = 0 ; i < opType ; i++){					
					if(!executeStack.empty()){						
						argToken = executeStack.pop();						
						if(ExpressionToken.ETokenType.ETOKEN_TYPE_CONSTANT == argToken.getTokenType()){
							args[i] = argToken.getConstant();							
						}else{
							//如果取出的Token不是常量，则抛出错误
							throw new IllegalStateException("操作符" + expToken.getOperator().getToken() + "找不到相应的参数，或参数个数不足;位置：" + expToken.getStartPosition());						
						}
					}else{
						//栈已经弹空，没有取道操作符对应的操作数
						throw new IllegalStateException("操作符" + expToken.getOperator().getToken() + "找不到相应的参数，或参数个数不足;位置：" + expToken.getStartPosition());						
					}
				}
				//执行操作符
				Constant result = expToken.getOperator().execute(args);
				System.out.println(expToken.getOperator().toString() + " -- > " + result.getStringValue());
				ExpressionToken resultToken =  ExpressionToken.createConstantToken(result);
				//将执行结果压入栈
				executeStack.push(resultToken);
				
			}else if (ExpressionToken.ETokenType.ETOKEN_TYPE_FUNCTION == expToken.getTokenType()){
				
				if(!executeStack.empty()){
					
					ExpressionToken onTop = executeStack.pop();
					//检查在遇到函数词元后，执行栈中弹出的第一个词元是否为“）”
					if(")".equals(onTop.getSplitor())){
						
						boolean doPop = true;
						List<Constant> argsList = new ArrayList<Constant>();
						ExpressionToken parameter = null;
						//弹出函数的参数，直到遇到"("时终止
						while(doPop && !executeStack.empty()){
							parameter = executeStack.pop();
							
							if(ExpressionToken.ETokenType.ETOKEN_TYPE_CONSTANT == parameter.getTokenType()){
								argsList.add(parameter.getConstant());
							}else if("(".equals(parameter.getSplitor())){
								doPop = false;
							}else{
								//在函数中遇到的既不是常量，也不是"(",则报错
								throw new IllegalStateException("函数" + expToken.getFunctionName() + "执行时遇到非法参数" + parameter.toString());						
							}
						}
						
						if(doPop && executeStack.empty()){
							//操作栈以空，没有找到函数的左括号（
							throw new IllegalStateException("函数" + expToken.getFunctionName() + "执行时没有找到应有的\"(\"" );						
						}
						
						//执行函数
						Constant[] arguments = new Constant[argsList.size()];
						arguments = argsList.toArray(arguments);
						Constant result = FunctionExecution.execute(expToken.getFunctionName(), expToken.getStartPosition() , arguments);
						ExpressionToken resultToken =  ExpressionToken.createConstantToken(result);
						//函数结果入栈
						executeStack.push(resultToken);
						
					}else{
						//没有找到应该存在的右括号
						throw new IllegalStateException("函数" + expToken.getFunctionName() + "执行时没有找到应有的\")\"" );						
					
					}
					
				}else{
					//没有找到应该存在的右括号
					throw new IllegalStateException("函数" + expToken.getFunctionName() + "执行时没有找到应有的\")\"" );						
				}

			}else if (ExpressionToken.ETokenType.ETOKEN_TYPE_SPLITOR == expToken.getTokenType()){
				//读取一个分割符，压入栈，通常是"(",")"
				if("(".equals(expToken.getSplitor()) 
						|| ")".equals(expToken.getSplitor())
						|| ",".equals(expToken.getSplitor())
						){
					executeStack.push(expToken);
					
				}else if ("{".equals(expToken.getSplitor())){
					//读取第一个{
					if(inShortCutStatus == 0){
						if(!executeStack.empty()){
							//取栈顶的boolean值
							ExpressionToken onTop = executeStack.peek();
							//如果栈顶boolean为false
							if(!onTop.getConstant().getBooleanValue()){
								//设置短路信号
								inShortCutStatus--;
								//将{压入栈顶
								executeStack.push(expToken);
							}else{
								//如果栈顶boolean为true,则直接忽略{
							}

						}else{
							//没有找到应该存在的&&运算的右边表达式
							throw new IllegalStateException("&&运算参数错误，没有发现右边布尔表达式" );						
						}
					}
					
				}else if ("}".equals(expToken.getSplitor())){
					//读取第一个}
					if(inShortCutStatus == 0){
						if(!executeStack.empty()){
							//取栈顶的boolean值
							ExpressionToken onTop = executeStack.peek();
							//如果栈顶boolean为true
							if(onTop.getConstant().getBooleanValue()){
								//设置短路信号
								inShortCutStatus++;
								//将}压入栈顶
								executeStack.push(expToken);
							}else{
								//如果栈顶boolean为false,则直接忽略}
							}
							
						}else{
							//没有找到应该存在的||运算的左边表达式
							throw new IllegalStateException("||运算参数错误，没有发现左边布尔表达式" );						
						}
					}
				}

			}			
		}
		
		//表达式执行完成，这是执行栈内应该只有一个结果
		if(executeStack.size() == 1){
			return executeStack.pop();
		}else{
			StringBuffer errorBuffer = new StringBuffer("\r\n");
			while(!executeStack.empty()){
				ExpressionToken onTop = executeStack.pop();
				errorBuffer.append("\t").append(onTop.toString()).append("\r\n");
			}
			throw new IllegalStateException("表达式不完整.\r\n 结果状态异常:" + errorBuffer);						
		}
	}
	
	/**
	 * 将子窜转化成Token并加入列表
	 * @param tokenString
	 * @param tokens
	 * @throws IllegalExpressionException 
	 */
	private void addToken(String tokenString , List<ExpressionToken> tokens) throws IllegalExpressionException{
		
		ExpressionToken token = null;
		//null
		if( ExpressionTokenHelper.isNull(tokenString)){
			token = ExpressionToken.createConstantToken(BaseDataMeta.DataType.DATATYPE_NULL , null);
			tokens.add(token);
			
		}else
		//boolean
		if(ExpressionTokenHelper.isBoolean( tokenString)){
			token = ExpressionToken.createConstantToken(BaseDataMeta.DataType.DATATYPE_BOOLEAN , Boolean.valueOf(tokenString));
			tokens.add(token);
			
		}else 
		//integer
		if(ExpressionTokenHelper.isInteger(tokenString)){
			token = ExpressionToken.createConstantToken(BaseDataMeta.DataType.DATATYPE_INT , Integer.valueOf(tokenString));
			tokens.add(token);
			
		}else
		//long 
		if( ExpressionTokenHelper.isLong(tokenString)){
			token = ExpressionToken.createConstantToken(BaseDataMeta.DataType.DATATYPE_LONG , Long.valueOf(tokenString.substring(0 , tokenString.length() - 1)));
			tokens.add(token);
			
		}else
		//float 
		if( ExpressionTokenHelper.isFloat(tokenString)){
			token = ExpressionToken.createConstantToken(BaseDataMeta.DataType.DATATYPE_FLOAT , Float.valueOf(tokenString.substring(0 , tokenString.length() - 1)));
			tokens.add(token);
			
		}else
		//double
		if(ExpressionTokenHelper.isDouble(tokenString)){
			token = ExpressionToken.createConstantToken(BaseDataMeta.DataType.DATATYPE_DOUBLE , Double.valueOf(tokenString));
			tokens.add(token);

		}else				
		//Date 
		if(ExpressionTokenHelper.isDateTime(tokenString)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				token = ExpressionToken.createConstantToken(BaseDataMeta.DataType.DATATYPE_DATE , sdf.parse(tokenString.substring(1 , tokenString.length() - 1)));
			} catch (ParseException e) {
				e.printStackTrace();
				throw new IllegalExpressionException("日期参数格式错误");
			}
			tokens.add(token);
			
		}else
		//String
		if(ExpressionTokenHelper.isString(tokenString)){
			token = ExpressionToken.createConstantToken(BaseDataMeta.DataType.DATATYPE_STRING , tokenString.substring(1 , tokenString.length() - 1));
			tokens.add(token);
		}else
		//分割符
		if(ExpressionTokenHelper.isSplitor(tokenString)){
			token = ExpressionToken.createSplitorToken(tokenString);
			tokens.add(token);

		}else			
		//函数
		if(ExpressionTokenHelper.isFunction(tokenString)){
			token = ExpressionToken.createFunctionToken(tokenString.substring(1 , tokenString.length()));
			tokens.add(token);
			
		}else		
		//操作符
		if(ExpressionTokenHelper.isOperator(tokenString)){
			Operator operator = Operator.valueOf(tokenString);
			token = ExpressionToken.createOperatorToken(operator);
			tokens.add(token);

		}else{
			//剩下的都应该是变量，这个判断依赖于生成的RPN是正确的前提
			//变量,在boolean型和null型判别后，只要是字母打头的，不是$的就是变量
			token = ExpressionToken.createVariableToken(tokenString);
			tokens.add(token);
	
		}		
	}
	/**
	 * 执行操作符校验
	 * @param op
	 * @param verifyStack
	 * @return
	 */
	private ExpressionToken verifyOperator(ExpressionToken opToken , Stack<ExpressionToken> verifyStack)throws IllegalExpressionException{
		//判定几元操作符
		Operator op = opToken.getOperator();
		int opType = op.getOpType();
		//取得相应的参数个数
		BaseDataMeta[] args = new BaseDataMeta[opType];
		ExpressionToken argToken = null;
		for(int i = 0 ; i < opType ; i++){			
			if(!verifyStack.empty()){				
				argToken = verifyStack.pop();
				
				if(ExpressionToken.ETokenType.ETOKEN_TYPE_CONSTANT == argToken.getTokenType()){
					args[i] = argToken.getConstant();
					
				}else if(ExpressionToken.ETokenType.ETOKEN_TYPE_VARIABLE == argToken.getTokenType()){
//				//验证VariableContainer中变量声明
//					Variable var = VariableContainer.getVariable(argToken.getVariable().getVariableName());
//					if(var == null || var.getDataType() == null){
//						throw new IllegalExpressionException("表达式不合法，变量\"" + argToken.toString() + "\"缺少定义;位置：" + argToken.getStartPosition()
//								, opToken.toString()
//								, opToken.getStartPosition());						
//					}else{
//						//设置Token中的变量类型定义
//						argToken.getVariable().setDataType(var.getDataType());
//					}					
					args[i] = argToken.getVariable();
					
				}else{
					//如果取到的Token不是常量，也不是变量，则抛出错误
					throw new IllegalExpressionException("表达式不合法，操作符\"" + op.getToken() + "\"参数错误;位置：" + argToken.getStartPosition()
							, opToken.toString()
							, opToken.getStartPosition());						
				}
				
			}else{
				//栈已经弹空，没有取道操作符对应的操作数
				throw new IllegalExpressionException("表达式不合法，操作符\"" + op.getToken() + "\"找不到相应的参数，或参数个数不足;"					
								, opToken.toString()
								, opToken.getStartPosition());						
			}
		}
		//执行操作符校验，并返回校验
		Constant result = op.verify(opToken.getStartPosition() , args);
		return ExpressionToken.createConstantToken(result);		 

	}

	/**
	 * 执行函数校验
	 * @param op
	 * @param verifyStack
	 * @return
	 */
	private ExpressionToken verifyFunction(ExpressionToken funtionToken , Stack<ExpressionToken> verifyStack)throws IllegalExpressionException{
		
		if(!verifyStack.empty()){

			/*****
			 * 在转化RPN过程中进行校验时，函数的“)”括号不会压入verifyStack
			 * 但如果将RPN和校验分开，则必须检查栈顶的")"
			 */
//			ExpressionToken onTop = verifyStack.pop();
//			//检查在遇到函数词元后，执行栈中弹出的第一个词元是否为“）”
//			if(")".equals(onTop.getSplitor())){
				
				boolean doPop = true;
				List<BaseDataMeta> args = new ArrayList<BaseDataMeta>();
				ExpressionToken parameter = null;
				//弹出函数的参数，直到遇到"("时终止
				while(doPop && !verifyStack.empty()){
					parameter = verifyStack.pop();
					
					if(ExpressionToken.ETokenType.ETOKEN_TYPE_CONSTANT == parameter.getTokenType()){
						//常量
						args.add(parameter.getConstant());
						
					}else if(ExpressionToken.ETokenType.ETOKEN_TYPE_VARIABLE == parameter.getTokenType()){
//						//验证变量声明	
//						Variable var = VariableContainer.getVariable(parameter.getVariable().getVariableName());
//						if(var == null || var.getDataType() == null){
//							throw new IllegalExpressionException("表达式不合法，变量\"" + parameter.toString() + "\"缺少定义;位置:" + parameter.getStartPosition()
//										, parameter.toString()
//										, parameter.getStartPosition());						
//						}else{
//							//设置Token中的变量类型定义
//							parameter.getVariable().setDataType(var.getDataType());
//						}
						args.add(parameter.getVariable());
						
					}else if("(".equals(parameter.getSplitor())){
						doPop = false;
						
					}else{
						//没有找到应该存在的右括号
						throw new IllegalExpressionException("表达式不合法，函数\"" + funtionToken.getFunctionName()+ "\"遇到非法参数" + parameter.toString() + ";位置:" + parameter.getStartPosition()
								, funtionToken.toString()
								, funtionToken.getStartPosition());						
					}
				}
				
				if(doPop && verifyStack.empty()){
					//操作栈以空，没有找到函数的左括号（
					throw new IllegalExpressionException("表达式不合法，函数\"" + funtionToken.getFunctionName() + "\"缺少\"(\"；位置:" + (funtionToken.getStartPosition() + funtionToken.toString().length())
							, funtionToken.toString()
							, funtionToken.getStartPosition());						
				}
				
				//校验函数
				BaseDataMeta[] arguments = new BaseDataMeta[args.size()];
				arguments = args.toArray(arguments);
				Constant result = FunctionExecution.varify(funtionToken.getFunctionName(), funtionToken.getStartPosition() , arguments);
				return ExpressionToken.createConstantToken(result);
				
//			}else{
//				//没有找到应该存在的右括号
//				throw new IllegalExpressionException("表达式不合法，函数\"" + funtionToken.getFunctionName() + "\"缺少\")\";位置:" + onTop.getStartPosition()
//						, funtionToken.toString()
//						, funtionToken.getStartPosition());
//				
//			}
			
		}else{
			//没有找到应该存在的右括号
			throw new IllegalExpressionException("表达式不合法，函数\"" + funtionToken.getFunctionName() + "\"不完整" 
					, funtionToken.toString()
					, funtionToken.getStartPosition());
		}
	}
	
	
	public static void main (String[] args){
		ExpressionExecutorInCoding ee = new ExpressionExecutorInCoding();
		//String example = "\"aa\" + (false:1)";
		//String example = "$STARTSWITH(\"hahahaha\", \"hahe\")";
		String example = "$ENDSWITH(\"hahahaha\", \"haha\")";
		//String example = "true != !$DAYEQUALS($CALCDATE($SYSDATE() ,0,0 , (8+11-5*(6/3)) * (2- 59 % 7),0 ,0,0 ) , [2008-10-01])";  
		
		//String example = "8+11-5*(6/3)";  
		//String example = "\"12345\" <= \"223\"";
		//String example = "12345 <= 223";
		//String example = "[2007-01-01] <= [2008-01-01]";
		
		//String example = "\"12345\" >= \"223\"";
		//String example = "12345 >= 223";
		//String example = "[2007-01-01] >= [2008-01-01]";
		
		//String example = "\"12345\" < \"223\"";
		//String example = "12345 < 223";
		//String example = "[2007-01-01] < [2008-01-01]";

		//String example = "\"12345\" >\"223\"";
		//String example = "12345 > 223";
		//String example = "[2007-01-01] > [2008-01-01]";

		
		//String example = "\"12345\" == \"223\"";
		//String example = "223 == 223.0";
		//String example = "[2008-01-01] == [2008-01-01]";
		//String example = "true == $DAYEQUALS([2008-01-01] , [2008-11-01])";
		//String example = "null == $DAYEQUALS([2008-01-01] , [2008-11-01])";
		
		//String example = "\"12345\" != \"223\"";
		//String example = "12345 != 223.0";
		//String example = "[2008-01-01] != [2008-01-01]";
		//String example = "true != $DAYEQUALS([2008-01-01] , [2008-11-01])";
		//String example = "null != $DAYEQUALS([2008-01-01] , [2008-11-01])";

		//String example = "true || $DAYEQUALS([2008-01-01] , [2008-11-01])";

		//String example = "true && $DAYEQUALS([2008-01-01] , [2008-11-01])";
		
		//String example = "$DAYEQUALS([2008-11-01] , [2008-11-01]):\"日期相等\"#1+2+3#$SYSDATE()";
		//String example = " (2000 >= 1000 : \"路径1\") + (2000 < 1000 : \"路径2\" + \"aaaa\") ";
		//String example = "$DAYEQUALS($CALCDATE([2008-11-01 00:00:00],0,0,-2,0,0,0) , [2008-10-31 23:00:00])";
		//String example = "100<10 && (333<3333 || 5555> 666) || false";
		try {
			List<ExpressionToken> list = ee.analyze(example);			
			list = ee.convertToRPN(list);			
			
			String s1 = ee.tokensToString(list);
			System.out.println("s1 -- " + s1);
			List<ExpressionToken> tokens = ee.stringToTokens(s1);
			String s2 = ee.tokensToString(tokens);
			System.out.println("s2 -- " + s2);
			System.out.println("s1 == s2 ? " + s1.equals(s2));

			System.out.println(ee.executeRPN(tokens));
		} catch (IllegalExpressionException e) {

			e.printStackTrace();
		}		
	}	
}