### Sek-2016
####Codigo Sek 2016 Pequi Mecânico.  
Este é o nosso arquivo readme, qualquer coisa que considerar viável para o entendimento do projeto pode ser escrita aqui.  
Temos também a wiki do repositório que pode ser usada para anotações e explicações mais detalhadas sobre alguma parte de algum projeto.

--
####Como importar do gitHub diretamente para o eclipse:
1. Primeiro entre no site do Github, vá no repositorio <https://github.com/rogertecnic/Sek-2016>, clique no botão verde (clone or download) e copie o link, ou copie esse link: https://github.com/rogertecnic/Sek-2016.git  
2. No Eclipse, va em File>import>Git>Clone URI: cole o link no campo URI, (pode preencher o Authentication com a sua conta do gitHub), NEXT;  
3. Selecione os branchs que deseja copiar, (branchs são copias inteiras paralelas do repositorio podendo ser alteradas separadamente e depois "juntadas"), NEXT;  
4. ***ATENÇÃO*** Selecione o local onde salvará o repositorio git local O PROJETO FICA DENTRO DO REPOSITORIO GIT, NÃO FICA DENTRO DO WORKSPACE, NEXT;  
5. Import Existing Eclipse Project, NEXT;  
6. ***ATENÇÃO*** Marque a caixa **Search for nested project** e *NÃO* marque o projeto "sek-2016", marque somente o "GhostName" e clique em FINISH, se caso a opção estiver indisponível em cinza é porque ja exite um projeto com o mesmo nome no seu eclipse no workspace aberto no seu eclipse, NÃO importe o sek-2016, ele não é um projeto, é a pasta repositorio, clique em cancel, o repositorio ja foi importado para o pc e o projeto esta dentro dele, neste caso siga estes paços:  

> Modifique o nome ou delete o projeto ja existente do seu workspace;  
>va em File>Import>Projects from Git>Existing local repository, NEXT;  
>selecione o repositorio Sek-2016 que ja está no seu pc, NEXT;  
>deixe marcado a pasta "Working tree", selecione Import "existing eclipse project", NEXT;  
>faça o mesmo procedimento descrito no final do paço 6, ***não importe o Sek-2016***;

***Favor não dar commit and push no protheus se não tiver certeza do que está upando para o github, sempre dar um pull antes de começar a editar o codigo para ver se tem nova alteração no codigo, caso contrario podera haver conflitos de alterações, dar o commit and push sempre ao final do trabalho, comentando direitinho o que foi feito no codigo***
--

####Termos comuns:
* ***Reset***: Reiniciar a JVM (Java Virtual Machine) saindo da Thread Main e encerrando TODA a execução do código, voltando ao menu do EV3;

* ***Reinicio***: Reiniciar somente a Thread do programa, sem encerrar a execução da Thread Main, volta para o menu do codigo;


*vale a pena ler*: Como o código java é compilado e executado: http://www.devmedia.com.br/entenda-como-funciona-a-java-virtual-machine-jvm/27624

---
Este arquivo utiliza formato de texto MarkDown (padrão do github para Readme files).
