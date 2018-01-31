
 package CompArch;
 
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import CompArch.Processor.Cycles;
import CompArch.Processor.Instruction_Status;
import Managers.IOManager;
import Managers.MemoryManager;
import Managers.RegisterManager;
import CompArch.Processor.Instruction_Name;

/**
 * @author Abhinav Gundlapalli
 * @id 1454321
 * @email agundlapalli@uh.edu
 */

public class Simulator {
	// variables
    private static HashMap<String, Integer> bznLoc = new HashMap<String, Integer>(); 
    private static int       InstNum,PC,CC;        
    private static boolean   Stall,Flush;                
    private static String    inFile,outFile;         
    private static BufferedReader bufAction = new BufferedReader(new InputStreamReader(System.in));
    
    
    public static void main(String[] args) {
            System.out.println("\t \t \tADAVANCED TOPICS IN COMPUTER ARCHITECTURE ECE-7373 SPRING 2016");
            System.out.println("\t \t \t \t \t COURSE PROJECT");
            System.out.println("\t \t \t \t     MIPS SIMULATOR (8-Stage)\u00a9");
            System.out.println("\t \t \t \t       Abhinav Gundlapalli");
            System.out.println("Enter the Input File (with path): ");
            try {
				inFile = bufAction.readLine();
				//inFile="E:\\Masters\\ECE_7373_Comp_Arch\\Project\\Input\\input.txt";
			} catch (Exception e) {
				System.err.println("Can't Process Input");
				e.printStackTrace();
			}
            System.out.println("Enter the Output File (with path): ");
            try {
				outFile = bufAction.readLine();
            	//outFile="E:\\Masters\\ECE_7373_Comp_Arch\\Project\\output\\output.txt";
			} catch (Exception e) {
				System.out.println("error Processing Output");
				e.printStackTrace();
			}
            Simulator objSimulator = new Simulator();
            objSimulator.run();
            IOManager.writeOutFile();
            System.out.println("SIMULATION COMPLETE");
    }
    
    public void run() {
        InstNum = 1;
        CC = 1;
        PC = 0;
        MemoryManager.Mem_Map.clear();
        bznLoc.clear();
        Processor.getInsQ().clear();
        Pipeline.getPipelines().clear();
        RegisterManager.setRegisters(new RegisterManager[32]);
        Stall = false;
        Flush = false;
        IOManager.outPutContent.setLength(0);
        IOManager objIOManager = new IOManager();
        objIOManager.ReadInput(inFile);
        while (check_Run()) {
            IOManager.fileOut("C#" + CC+" \t", false);
            WriteBack(); 
            MemoryManager.MEM3();        
            MemoryManager.MEM2();         
            MemoryManager.MEM1();
            Execute();           
            Instruction_Decode(); 
            Instruction_Fetch2(); 
            Instruction_Fetch1(); 
            CC++;
            IOManager.toOutfile("\r");
        }
        IOManager.Registers_Write();
    }
    public void Instruction_Fetch1() {
        if((Simulator.PC < Processor.getInsQ().size()) && !Stall) {
            String rawInst = Processor.getInsQ().get(PC);    
            Processor Inst = new Processor(rawInst);        
            Pipeline if1 = new Pipeline(Inst);           
            Inst.setCurCycle(Cycles.IF1);                       
            Pipeline.getPipelines().addFirst(if1);                  
            if1.getInst().Current_Stage();                       
            Simulator.PC = Simulator.PC + 1;                
        } else if (!Stall) {
        	Pipeline.getPipelines().addFirst(null);
        }
    }
    
    public void Instruction_Fetch2() {
        Pipeline if2 = IOManager.getAddress(2);
        if (if2 != null) {
            if(!Stall) {
                if2.getInst().nxtCycleProcessor();
                if2.getInst().Current_Stage();
            } else {
                IOManager.Stall_appender(if2.getInst().getInstNum());
            }
        }
    }

    public void Instruction_Decode() {
        Pipeline id = IOManager.getAddress(3);
        if (id != null) {
            if(!Stall) {
                id.getInst().decode();
                id.getInst().nxtCycleProcessor();
                id.getInst().Current_Stage();
            } else {
                IOManager.Stall_appender(id.getInst().getInstNum());
            }
        }
    }

    public void Execute() {
        Pipeline ex = IOManager.getAddress(4);
        if (ex != null) {
            Simulator.Stall = ex.Stall();
            if (!Stall) {
                if (ex.getInst().getCntrl().isALUOp()) {
                    int result=0, tmpA=0, tmpB=0;
                    tmpA = ex.getInst().getSrc1().rdOpVal();
                    tmpB = ex.getInst().getSrc2().rdOpVal();
                    if (ex.getInst().getName() == Instruction_Name.SUB)
                        result = tmpA - tmpB;
                    else if (ex.getInst().getName() == Instruction_Name.DADD)
                        result = tmpA + tmpB;
                    ex.getALUresult().assignValue(result);
                } else if (ex.getInst().getName() == Instruction_Name.BNEZ) {
                    int src1Val = ex.getInst().getSrc1().rdOpVal();
                    boolean tmpBool = (src1Val == 0) ? false : true;
                    ex.setResAftrCond(new Boolean(tmpBool));
                } else if (ex.getInst().getName() == Instruction_Name.LD) {
                    ex.getALUresult().assignValue(ex.getInst().getSrc1().rdOpVal());
                } else if (ex.getInst().getName() == Instruction_Name.SD) {
                    ex.getALUresult().assignValue(ex.getInst().getDest().rdOpVal());
                }
                ex.getInst().nxtCycleProcessor();
                ex.getInst().Current_Stage();
            } else {
                IOManager.Stall_appender(ex.getInst().getInstNum());
                Pipeline.getPipelines().add(3,null);
            }
        }
    }
    
    public void WriteBack() {
        Pipeline objPipeline = IOManager.getAddress(8);
        int val = 0;
        if (objPipeline != null) {
            if (objPipeline.getInst().getCntrl().isWritetoaReg()) {
                if (objPipeline.getInst().getCntrl().isMemToReg())
                    val = objPipeline.getLdmemDataReg().valueRead();
                else
                    val = objPipeline.getALUresult().valueRead();
                objPipeline.getInst().Register_Write(val);
            }
            objPipeline.getInst().nxtCycleProcessor();
            objPipeline.getInst().Current_Stage();
            objPipeline.getInst().setStatus(Instruction_Status.COMPLETE);
            Pipeline.getPipelines().removeLast();
        } else if (Pipeline.getPipelines().size() >= 7) {
        	Pipeline.getPipelines().removeLast();
        }
    }
    
    public boolean check_Run() {
        if (Pipeline.getPipelines().size() < 7)
            return true;
        for (Pipeline pl : Pipeline.getPipelines())
            if (pl != null)
                return true;
        return false;
    }


    public static final HashMap<String, Integer> getBranchLoc() {
		return bznLoc;
	}

	public static final void setBranchLoc(HashMap<String, Integer> branchLoc) {
		Simulator.bznLoc = branchLoc;
	}

	public static final int getInstnum() {
		return InstNum;
	}

	public static final void setInstnum(int instnum) {
		InstNum = instnum;
	}

	public static final boolean isStall() {
		return Stall;
	}

	public static final void setStall(boolean isStall) {
		Simulator.Stall = isStall;
	}

	public static final boolean isFlush() {
		return Flush;
	}

	public static final void setFlush(boolean isFlush) {
		Simulator.Flush = isFlush;
	}

	public static final int getPC() {
		return PC;
	}

	public static final void setPC(int pC) {
		PC = pC;
	}

	public static final int getClockCycle() {
		return CC;
	}

	public static final void setClockCycle(int clockCycle) {
		Simulator.CC = clockCycle;
	}

	public static final String getInFile() {
		return inFile;
	}

	public static final void setInFile(String inFile) {
		Simulator.inFile = inFile;
	}

	public static final String getOutFile() {
		return outFile;
	}

	public static final void setOutFile(String outFile) {
		Simulator.outFile = outFile;
	}

	public static final BufferedReader getBufAction() {
		return bufAction;
	}

	public static final void setBufAction(BufferedReader bufAction) {
		Simulator.bufAction = bufAction;
	}

	
}