FROM  centos:latest 

LABEL authors="mark@ukotic.net,imoimo@gmail.com" \
      version="1.0" \
	  description="vSphere PowerCLI container running PowerShell and Shellinabox"


RUN curl https://packages.microsoft.com/config/rhel/7/prod.repo | tee /etc/yum.repos.d/microsoft.repo && \
    yum install -y epel-release && \
    yum install -y powershell openssh-server tmux supervisor shellinabox && \
    yum clean all && rm -rf /var/cache/yum

RUN rm -f /etc/ssh/ssh_host_ecdsa_key /etc/ssh/ssh_host_rsa_key && \
    ssh-keygen -q -N "" -t dsa -f /etc/ssh/ssh_host_ecdsa_key && \
    ssh-keygen -q -N "" -t rsa -f /etc/ssh/ssh_host_rsa_key  && \
    sed -i "s/#UsePrivilegeSeparation.*/UsePrivilegeSeparation no/g" /etc/ssh/sshd_config && \
    sed -i "s/UsePAM.*/UsePAM yes/g" /etc/ssh/sshd_config && \
    ssh-keygen -A && \ 
    mkdir /var/run/sshd

RUN useradd powercli -m -d /home/powercli/ -s /bin/bash	&& \ 
    echo "tmux new-session pwsh" >> ~powercli/.bashrc && \
    echo 'powercli:password' | chpasswd && \
    sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd

ENV NOTVISIBLE "in users profile"
ENV HOSTNAME vsphere
RUN echo "export VISIBLE=now" >> /etc/profile

COPY supervisord.conf /etc/supervisord.conf
COPY certificate.pem /certificate.pem

SHELL [ "pwsh", "-command" ]
RUN Set-PSRepository -Name PSGallery -InstallationPolicy Trusted; \
    $ProgressPreference='SilentlyContinue'; \
    Install-Module VMware.PowerCLI,PowerNSX,PowervRA -verbose:$false > $null; \
    Set-PowerCLIConfiguration -InvalidCertificateAction Ignore -Scope AllUsers -Confirm:$false; \
    Set-PowerCLIConfiguration -ParticipateInCEIP $false -Scope AllUsers -Confirm:$false; \
	$ProgressPreference='Continue'

EXPOSE 22 4200
CMD ["/usr/bin/supervisord"]
