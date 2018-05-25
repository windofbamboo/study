################################################################################
include $(BUDE_HOME)/stdmake/stdenv
################################################################################
SUBDIRS          =
AFTER_SUMMIT     =

################################################################################
SRCS             = 	baseUtil.cpp \
										ZkEnum.cpp \
										ZkOutput.cpp \
										ZkActiveValue.cpp \
										ZkFileTypeValue.cpp \
										jsonUtil.cpp \
										ExceptionErrCode.cpp \
										ZK_Exception.cpp \
										ZkWrapper.cpp \
										ResZkPathOperator.cpp \
										ResInfoZkClient.cpp

SUBMIT_HDRS      =  baseUtil.h \
										ZkEnum.h \
										ZkOutput.h \
										ZkActiveValue.h \
										ZkFileTypeValue.h \
										jsonUtil.h \
										ExceptionErrCode.h \
										ZK_Exception.h \
										ZkWrapper.h \
										ResZkPathOperator.h \
										ResInfoZkClient.h

#生成目标
PROGRAM          =
STATIC_LIBRARY   = libroam_frame.a
SHARE_LIBRARY    =

#提交相关变量
DEST    = #$(WORK_HOME)/bin
DESTLIB = $(WORK_HOME)/lib/roam
DESTINC = $(WORK_HOME)/include/roam/frame
DESTSRC =
DESTDOC =

################################################################################
THE_INCLUDE      = -I. \
									 -I/usr/include \
									 -I$(WORK_HOME)/include/zookeeper \
									 -I$(WORK_HOME)/include/rapidjson

THE_LIBPATH      = -L./  \
                   -L$(WORK_HOME)/lib/zookeeper \
                   -L$(WORK_HOME)/lib/roam

THE_LIB          = 

################################################################################
CXXFLAGS = ${THE_INCLUDE} ${SYSCOMP_INCLUDE} ${ORA_INCLUDE} -DXP_UNIX  -DJSFILE -DHAVE_SYS_INTTYPES_H -DFP_DEBUG
CCFLAGS  = ${THE_INCLUDE} ${SYSCOMP_INCLUDE} ${ORA_INCLUDE} -DXP_UNIX  -DJSFILE -DHAVE_SYS_INTTYPES_H -DFP_DEBUG
LDFLAGS  = ${THE_LIBPATH} ${SYSCOMP_LIBPATH} ${ORA_LIBPATH} -bbigtoc
LDLIB    = ${THE_LIB}
################################################################################
include $(BUDE_HOME)/stdmake/stdmk