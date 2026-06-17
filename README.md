fazer um plugin proj imagej

Plugins are implemented as [[java]] [[classe]]s, which means that you can use all features of the Java language, access the full ImageJ API and use all standard and third-party Java APIs in a plugin

There are basically two types of plugins: those that do not require an image as input (implementing the interface PlugIn) and plugin filters, that require an image as input (implementing the interface PlugInFilter). A PlugInFrame is a plugin that runs in its own window.


// arquivo de pre processamento
// arquivo exgtrator
// arquivo de ctiação de saída final

pré fazer o plugin
- [x] instalar imagej
- [x] instalar JRE e compilador java (instalei intelij)
- [ ] 
oq ele tem q fazer?

//eu
- [ ] O usuário abre uma imagem de referência; (ref_image)
- [x] O usuário abre uma pasta onde está armazenada a base de imagens de busca; (search_directory)
- [x] O usuário define os parâmetros de execução do extrator de características;
	- [ ] Não há parâmetros a serem passados ao extrator.
- [ ] O usuário dispara a execução do extrator;

// criar estrutura = leozin
- [ ] O extrator **gera um vetor de características** para a img de referência e um vetor para cada img de busca e armazena em um arquivo; 
	- [ ] O vetor de características será composto pelos **valores de diâmetro efetivo, circularidade, arredondamento e razão de raio**.


- [ ] O usuário define um valor de k para realizar busca aos k-vizinhos mais próximos (material de apoio: https://drive.google.com/file/d/1D18ASFBaH9__edBU7WJLM2XlNekm3cbh/view?usp=drive_link); 


- [ ] O usuário escolhe uma função de distância a ser utilizada;
- [ ] O usuário dispara a busca aos k-vizinhos; 



- [ ] O sistema **gera um arquivo** (ou apresenta na tela) o vetor da img de referência e as k imgs de busca, com os respectivos vetores. 


**IMPORTANTE**: a otimização de desempenho na implementação do extrator será levada em consideração na avaliação do trabalho.
Medidas Geométricas (Leonardo e Natálias) 
- Não há parâmetros a serem passados ao extrator. 
- O vetor de características será composto pelos valores de diâmetro efetivo, circularidade, arredondamento e razão de raio.


// otsu precisa de 8-bit


2.6.2 PlugInFilter 

This interface also has a method void run(ImageProcessor ip) 

This method runs the plugin, what you implement here is what the plugin actually does. 

It takes the image processor it works on as an argument. 

The processor can be modified directly or a new processor and a new image can be based on its data, so that the original image is left unchanged. 
The original image is locked while the plugin is running. 

In contrast to the PlugIn interface the run method does not take a string argument. 

The argument can be passed using int setup(java.lang.String arg, ImagePlus imp) 

This method sets up the plugin filter for use. The arg string has the same function as in the run method of the PlugIn interface. 

You do not have to care for the argument imp—this is handled by ImageJ and the currently active image is passed. 

The setup method returns a flag word that represents the filters capabilities (i.e. which types of images it can handle)
