# Amazon e Sprin - Curso Alura
Introdução ao uso dos serviços da AWS para deploy de aplicações no cloud da amazon.
- Precisa ter acesso a conta Amazxon AWS, conta free ou completo.
- Habilitar a conta, lembre-se de aprovar o cadastro no email de confirmação.
- Mandatório adicionar um cartão de crédito ao cadastro.


# CloudWatch
Primeiramente cadastre um Alerta para ser informado sobre o custo de uso.
Na função CLoudWatch, é possivel confiogurar um Alerta de Faturamento(Billing),
este alerta iurá comunica-lo de gastos excedente ao que vc está disposot a gastar.
No caso, se vc não utrapassar o limite do uso gratuito não irá gastar nada, por um tempo.



# AWS S3
Serviço de armazenamaento no cloud. após ter cadastrado o seu Alerta, no CloudWatch, vamos cadastrar o BUCKET
para armazenar os arquivos que queremos, seja imagem, txt, etc. No meu caso cadastrei imagens.
Não é possivel ter mais de um BUCKET com o mesmo nome, ou seja você terá que cadastrar um Bucket para cada serviço, ou funcionalidade, a dica é criar um bucket com o nome do serviço e adicionar na nomenclatura um nome que vc queira e faça sentido para você, veja exemplo: "casodocodifo-fabianoss";
Criar um backet é teoricamente simples, ir no menu de serviço amazon, entrar no S3 e criar o bucket, dar o nome e  "NNF".
Para segurança devemos cirar acesso restrito para nossa aplicação, segue detlahes abaixo no IAM

# AWS IAM
Serviço visa criar a permissão de acesso ao bucket, para criar procure o serviço IAM no menu de serviço da AWS, acesse, clique em usuários, crie um novo, de um nome para ele e defina o tipo de acesso, se for a aplicação geralmente é informado como "acesso programatico".
Ao termino será gerado as chaves para uso na implementação do acesso da aplicação. baixoe o arquivo com as chaves que surgira no final da criação da chave, importante destacar que essa chaves só estaraão neste arquivo, não será possivel consulta-la novamente, a não ser que vc crie um novo usuário.

#Configurando o acesso na aplicação.
Adiconar a dependencia do componente da AWS "spring-cloud-aws-context", ela fornece acesso através de metodos que validam as credencias.
Na sua aplicação precisamos criar uma classe de configuração para usarmos as credencias de acesso ao Bucket.
Para issso criamos algumas variaveis com as chaves e metodos que utilizam essas informações.

Nesse projeto da caso do código, iremos usar nosso bucket para armazenar nossas imagens, nesse primeiro case, cadastraremos as imagens dos livros.
Para tal devemos ir na implementação do metedo que grava as imagens, neste primeiro momento nós estavamos armazenando elas no Banco de dados e usando um path de deploy do TomCat, agora iremos alterar a implelentação, usar nossos recursos da classe de configração.
Devemos olhar como é feito esse trabalho com a AWS, para isso devemos visitar o a documentação da amazon AWS "Working AWS S3", e saber ocmo podemos usar o acesso ao path do bucket que criamos.
Ao entender um pouco, notei que eu precisava indicar a região e o nome do bucket na montagem da url, ela será usada tanto para enviar como resgatar a imagem no bucket.

Após esses ajustes, precisei ajustar as paginas que estavam referenciando o path local do TomCat, e deixar somente a variavel que tras a informação da url.

Deixei comentado no código das paginas (home.jsp, detalhe.jsp e  itens.jsp).

Obs, para não aparecer mais os testes realizados antes com as imagens locais, devemos apagar os registros da base de dados.

Para permitir o acesso da aplciação ao bucket, além das configurações acima precisei que desabilitar as opções de "Bloquear acesso público" na aba de permissões do bucket dentro do S3.


# AWS RDS
Criação de base de Dados, a tabela de login que criamos para usar local agora configuramos os dados para usar Na AWS.
Abaixo dicas referente configuração da Instacia da base de dados em 2020.

Galera, seguem dicas aqui. Primeiramente o dashbord é bem diferente do que na época da gravação deste curso, apesar de ter todos os comandos indicados, porém foram adicionados novos campos, basicamente se seguir o que é dito vai dar certo. 
-  "Choose a database creation method" = Standard create, pois somente neste método aparecem todas a opções.
- "Edition" = esse passo é importante se vc usar a versão mais atual, lembre-se de atualizar a versão do drive no .pom da aplicação, e possíveis alterações, aqui rodou só ajustando a versão.
"Templates" = Free Tier
"VPC security group" = Selecione a opção "create new", crie um nome para o grupo VPC é obrigatório, eu usei "casadocodigo" e funcionou.
- Database options = expandir "Additional configuration" para colocar o nome da Instância da dabse de dados.
Sigas as instruções do video e revise com essas dicas mais recentes.


# AWS EC2
Permite criar as instâncias para deploy da aplicação, processo de criação é simples, mas devemos nos atentar a algumas coisas, definir quail a sub net que vc vai pedir para criar levando em conta a região que seus outros recursos estão, no caso criei meu bucket e RDS no norte da virginia, devemos criar a instância nesta mesma região, escolha uma das opções que aparecem no combo "subnet", após no combo "Auto-assign Public IP" habilite opara o acesso public
- para as regras de liberação de portas, como padrão soimente a por ta 22 é configurada para acesso publico.
No final Criar a chave de acesso remoto da instancia, por precisaremos para configurar e instalar a aplicação.

Para mandar a aplicação para instancia temos que configurar na isntancia o container o servidro de aplicação que nossa aplicação irá rodar.
Para fazer isso precisamos da ajuda do putty, e nele vamos pegar a chave .pem e converter para uma chave .ppk, assim o put terá acesso ao servidor linux, uma vez navegando no server conseguimos instalar os recursos necessário para o ambiente da aplicação, neste momento iremos instalar o tomcat e dar as devidas permissões de acesso para ele.

Após a conversão vá no menu de instancia e encontre o botão conectar, lá tem o endereço de acesso ao servidor, com o usuário ubuntu, antes de conectar configure a chave convertida (.ppk) na aba "OAUTH" do putty.

Quando chegar o moento de enciar o seu arquivo .war para o servidor linux, o ideal é fazer com uma ferramenta com protocolo scp, usei o "Cygwin", após instalação fazer a instalação usar ele através do prompt de comando (windows).

após tranferir o arquivo, conectar via ssh no ubuntu(putty) mudar as permissões do arquivo, mover para o diretório do servidor(neste meu caso o tomcat), e pra terminar dar o start do serviço do servidor.

Após configurarmos a aplicação foi necessário verificar as regras dos grupos de acesso, inicialmente configuramos o acesso a porta 8080 e 22 para todos os ips, porem a configuração do RDS estava somente para o ip da minha estação acessar, alterei para que todos pudessem acessar, porém tirei os direitos de acesso public do RDS, pois agora somente as redes da AWS tem acesso, ou seja o banco conversa somente com a aplicação e garante a segurança de acesso ao banco de dados.


# Auto Escalonamento(auto scaling)

- necessário criar grupo de escalonamento
- gerar imagem do servidor que deve ser replicado
- configurar essa imagem com o grupo de acessos necessários, não esquecer do acesso a porta 22 para o acesso remoto
- configurar a região de preferencia com uma região diferente da qual a imagem foi gerada.
- configurar a politica de criação de instacia, configurar a imagem criada para que a politica dispare criando com a imagem desejada;

# Load Balance
- Configurar load balance para o acesso das instancias e definir as regiões que eles atenderá
- configurar a porta de entrada dos clientes e a do servidor, neste caso a instancia estava usando para o tomcat a 8080
- definir novo grupo de acesso para acessar remotamente o load balance.
- configurar os sticks no load balance para a gerwenciamento de sessão, através do cokie que vai para o cliente o servidor se localiza de qual instancia iniciou a requisição.

# Xampp
software que ajuda a simular requisições, com ele conseguimos acionar o alarme para subir a segunda instancia.


# REDIS(ElasticCache)
Redis utilizamos para armazenar as sessões e auxiliar nos acessos das instancias.
Configurar o ambiente local para o teste depois subir na amazon, 2 tom,camt local com portas diferentes, e configurar o Profile do Spring, dando mobilidade a aplicação com perfil de dev e de produção.
Criar uma classe de configuração com as informações de comunicação do redis, metodo com a factory de conexão, metodo do template que consome a factory, e metodo Configure para sinalizar na amazon que vc não quer ouvir as mudanças de sessões no redis.
Instalar o Redis na Maquina local para teste.

Criando o Redis na Amazon, definir um grupo para as instancias criadas, usar para casa uma a vpc padrão que a Amazon disponibilizou, e adicionar na lista as 2 regiões, na aba de criação do grupo.

Após configurar, gerar novo .war com as configurações do Redis e perfil do Sprinf como "prod", enviar proservidor, dar permissão ao arquivo, parar o serviço do tomcat, deletar a versão antiga do .war de webapps, e mover a nova versão para webapps, e startar o serviço do tomcat, fazer este procediment para todas instancia que vc esta usando neste projeto.


