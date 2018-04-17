#!/bin/sh
if [ "x`uname -s`" = "xLinux" ]; then
    . $HOME/.bash_profile </dev/null 1>/dev/null 2>/dev/null
else
    . $HOME/.profile </dev/null 1>/dev/null 2>/dev/null
fi
#################################################################
#psinfo 获取ps信息
#env PMAP=1 PSOPT="" ps.sh match-string
#由于原先的rss包括了共享内存,但share选项也无输出,所以需要使用pmap进行重新计算.所以默认的ps.sh打印的是rss大小,加上env PMAP=1为剔除共享库和共享内存后的大小,和实际占用有出入,但可以用于判断是否有内存泄漏.
#另外,match-string为需要匹配的进程信息,减少屏幕输出.PSOPT为可以增加额外的ps选项,比如-L 打印子线程信息
#################################################################
#
#$1=cmd 命令
#$2=pid 进程号
#$3=动态库后缀
function psz_linux
{
#set -x
    if [ "x$1" = "x" -o "x$2" = "x" ]; then
        return 0; #为便于shell处理,不打印帮助信息
    fi
    eval ${1} ${2} |grep -v "^total "|grep -v "^---------"|grep -v awk|awk -v so="$3" 'BEGIN{i=1;totalmem=0;totalshare=0;}
    {
        if(index($0," shmid=")>0)
            totalshare=totalshare+$3;
        else if(index($0,so)>0)
            totalshare=totalshare+$3;
        else
        {
            if(i >= 3)  #第3行开始
            {
                totalmem=totalmem+$3;
            }
        }
        i++;
    }

    END{
        unit="";
        if(totalmem < 1024)
            ;
        else
        {
            if(totalmem >= 1048576)
            {
                totalmem=(int(totalmem*100/1024))/100;
                unit="M";
            }
            else
            {
                totalmem=(int(totalmem*100/1024))/100;
                unit="M";
            }
        }
        unitshare="";
        if(totalshare < 1024)
            ;
        else if(totalshare >= 1048576)
        {
            totalshare=(int(totalshare*100/1024))/100;
            unitshare="M";
        }
        else
        {
            totalshare=(int(totalshare*100/1024))/100;
            unitshare="M";
        }
        printf "%s%s(%s%s)\n",totalmem,unit,totalshare,unitshare;
    }'
}
#PMAP用于计算实际占用的内存,排除共享内存
#PSOPT用于增加额外的ps选项
#$1=grep_string
function psinfo
{
    osname="`uname -s`"
    cmdname=""
    pmapname=""
    grepstring="$1"
    soname=".so"
    if [ "x`uname -s`" = "xHP-UX" ]; then
        soname=".sl"
    fi
    if [ "x$osname" = "xHP-UX" ]; then
        cmdname="UNIX95= ps -ef $PSOPT -o 'pid,pcpu,vsz,nice,user,args'"
    elif [ "x$osname" = "xLinux" ]; then
        cmdname="ps ax $PSOPT -o 'pid,pcpu,rss,nice,user,args'"
        if [ "x$PMAP" = "x1" ]; then
            pmapname="pmap -x "
            logname="./.ps_linux.$$"
            trap "rm -f ${logname}* 2>/dev/null 1>/dev/null;exit 0" 1 2 15
            if [ "x$grepstring" != "x" ];then
                cmdname=$cmdname" | grep "$grepstring
            fi
            eval $cmdname |grep -v -w ps|grep -v -w ps.sh|grep -v -w awk|grep -v "PID"|grep -v -w grep|sort -n -k 3  > ${logname}
            #echo "       PID    %CPU      VSZ  NI        UID COMMAND"
            i=0;
            cp /dev/null ${logname}.1
            while read ifield
            do
                #echo "ifield="$ifield
                if [ "x$ifield" = "x" ]; then
                    continue;
                fi
                npid="`echo $ifield|awk '{print $1}'`"
                if [ "x$npid" = "x" ]; then
                    continue;
                fi
                if [ $i -eq 0 ]; then
                    echo "       PID    %CPU               VSZ(SHARE) NI        UID COMMAND"
                fi
                let i=$i+1
                vsz_linux="`psz_linux \"$pmapname\" \"$npid\" \"$soname\"`"
                #echo npid=$npid  vsz_linux=$vsz_linux
                echo $ifield |awk -v vszl=$vsz_linux '{n=split($0,a);
                                            for(j=1;j <= n;j++)
                                            {
                                                if(a[j] == "PID")
                                                    break;
                                                if(j == 3)      #VSZ
                                                {
                                                    printf "% 25s",vszl
                                                }
                                                else if(j == 1) #PID
                                                    printf "% 10s",a[j] ;
                                                else if(j == 2) #%CPU
                                                    printf "% 8s",a[j] ;
                                                else if(j == 4) #NI
                                                    printf "% 4s ",a[j] ;
                                                else if(j == 5) #UID
                                                    printf "% 10s ",a[j] ;
                                                else            #COMMAND
                                                    printf " %s",a[j] ;
                                            }
                                            if(a[j] != "PID")printf "\n";
                    }' >>${logname}.1
            done < ${logname}
            cat ${logname}.1|sort -k3
            echo "!!!Sort By VSZ(SHARE) Before PMAP-Adjust!!!"
            rm -f ${logname}* 2>/dev/null 1>/dev/null
            exit 0;
        fi
    else
        cmdname="ps -ef $PSOPT -o 'pid,pcpu,vsz,nice,user,args'"
    fi
    if [ "x$grepstring" != "x" ];then
        cmdname=$cmdname" | grep "$grepstring
    fi
    eval $cmdname |grep -v -w ps|grep -v -w ps.sh|grep -v -w awk|grep -v -w grep|sort -n -k 3|awk 'BEGIN{i=0;print "       PID    %CPU      VSZ  NI        UID COMMAND";}
    {   if(i == 0||$3 < 1024)
        {
            n=split($0,a);
            for(j=1;j <= n;j++)
            {
                if(a[j] == "PID")
                    break;
                if(j == 3)      #VSZ
                    printf "% 9s",a[j];
                else if(j == 1) #PID
                    printf "% 10s",a[j] ;
                else if(j == 2) #%CPU
                    printf "% 8s",a[j] ;
                else if(j == 4) #NI
                    printf "% 4s ",a[j] ;
                else if(j == 5) #UID
                    printf "% 10s ",a[j] ;
                else            #COMMAND
                    printf " %s",a[j] ;
            }
            if(a[j] != "PID")printf "\n";
            #print $0;
        }
        else
        {
            n=split($0,a);
            for(j=1;j <= n;j++)
            {
                if(a[j] == "PID")
                    break;
                if(j == 3)      #VSZ
                {
                    if(a[j] >= 1048576)
                        printf "% 8.1f%s",a[j]/1024,"M";
                    else
                        printf "% 8.1f%s",a[j]/1024,"M";
                }
                else if(j == 1) #PID
                    printf "% 10s",a[j] ;
                else if(j == 2) #%CPU
                    printf "% 8s",a[j] ;
                else if(j == 4) #NI
                    printf "% 4s ",a[j] ;
                else if(j == 5) #UID
                    printf "% 10s ",a[j] ;
                else            #COMMAND
                    printf " %s",a[j] ;
            }
            if(a[j] != "PID")printf "\n";
        }
        i++;
    }
    END{}'
}
psinfo $*
exit 0


